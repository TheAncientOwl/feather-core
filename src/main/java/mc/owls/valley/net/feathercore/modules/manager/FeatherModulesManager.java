package mc.owls.valley.net.feathercore.modules.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.logging.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import mc.owls.valley.net.feathercore.utils.StringUtils;

public class FeatherModulesManager {
    private static final String DEPENDENCY_GRAPH_BUILDER_FILE_NAME = "modules.json";

    private static class ModuleConfig {
        public Set<String> dependencies = new HashSet<>();
        public FeatherModule instance = null;
        public boolean mandatory = false;
    }

    private Map<String, ModuleConfig> moduleConfigs = new HashMap<>();
    private Map<String, FeatherModule> modules = new HashMap<>();
    private List<String> enableOrder = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T extends FeatherModule> T getModule(@NotNull final String name) {
        return (T) this.modules.get(name);
    }

    public void onEnable(@NotNull final FeatherCore plugin) throws ModuleSetupException {
        loadModules(plugin);
        computeEnableOrder();
        plugin.getFeatherLogger().debug("Enable order: " + String.join(", ", this.enableOrder));

        this.modules = this.moduleConfigs.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().instance));

        enableModules(plugin);

        this.moduleConfigs = null;
    }

    public void onDisable(@NotNull final IFeatherLoggger logger) {
        disableModules(logger);
    }

    private void loadModules(@NotNull final FeatherCore plugin) throws ModuleSetupException {
        // 1. load modules array from configuration file
        final JSONObject jsonConfig = FeatherModulesManager.loadJSON(plugin,
                FeatherModulesManager.DEPENDENCY_GRAPH_BUILDER_FILE_NAME);

        final JSONArray modulesArray = (JSONArray) jsonConfig.get("modules");
        assertEntryNotNull(modulesArray, "modules array");

        // 2. parse modules array
        for (final Object moduleObj : modulesArray) {
            final JSONObject moduleJSON = (JSONObject) moduleObj;

            final String name = (String) moduleJSON.get("name");
            final String className = (String) moduleJSON.get("class");
            final Boolean mandatory = (Boolean) moduleJSON.get("mandatory");
            final JSONArray dependencies = (JSONArray) moduleJSON.get("dependencies");

            assertEntryNotNull(name, "Some name property");
            assertEntryNotNull(className, name + " class property");
            assertEntryNotNull(mandatory, name + " mandatory property");
            assertEntryNotNull(dependencies, name + " dependencies property");

            final var module = new FeatherModulesManager.ModuleConfig();
            this.moduleConfigs.put(name, module);

            module.mandatory = mandatory;

            try {
                Class<?> clazz = Class.forName(className);
                module.instance = (FeatherModule) clazz.getConstructor(String.class).newInstance(name);
            } catch (Exception e) {
                throw new ModuleSetupException("Could not generate instance of class " + className + "\nReason: "
                        + StringUtils.exceptionToStr(e));
            }

            for (final Object dependencyObj : dependencies) {
                module.dependencies.add((String) dependencyObj);
            }

        }

        // 3. check if dependencies are actual modules
        for (final var moduleEntry : this.moduleConfigs.entrySet()) {
            final var moduleName = moduleEntry.getKey();
            final var moduleData = moduleEntry.getValue();

            for (final var dependency : moduleData.dependencies) {
                if (this.moduleConfigs.get(dependency) == null) {
                    throw new ModuleSetupException("Dependency '" + dependency + "' of module '" + moduleName
                            + "' does not name any valid module");
                }
            }
        }
    }

    private void computeEnableOrder() throws ModuleSetupException {
        // 1. build the graph and compute in-degrees
        final Map<String, HashSet<String>> graph = new HashMap<>();
        final Map<String, Integer> inDegrees = new HashMap<>();

        for (final var moduleName : this.moduleConfigs.keySet()) {
            inDegrees.put(moduleName, 0);
            graph.put(moduleName, new HashSet<>());
        }

        for (final var entry : this.moduleConfigs.entrySet()) {
            final var moduleName = entry.getKey();
            for (final var dependency : entry.getValue().dependencies) {
                graph.get(dependency).add(moduleName);
                inDegrees.put(moduleName, inDegrees.get(moduleName) + 1);
            }
        }

        // 2. perform topological sort using Kahn's algorithm
        final Queue<String> queue = new LinkedList<>();
        for (final var entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            final var moduleName = queue.poll();
            enableOrder.add(moduleName);

            for (final var neighhbor : graph.get(moduleName)) {
                inDegrees.put(neighhbor, inDegrees.get(neighhbor) - 1);
                if (inDegrees.get(neighhbor) == 0) {
                    queue.add(neighhbor);
                }
            }
        }

        // check for cycles
        if (enableOrder.size() != moduleConfigs.size()) {
            throw new ModuleSetupException(
                    "There is a cycle in the dependency graph of modules configuration. This is weird O.o, please contact the developer");
        }
    }

    private void enableModules(@NotNull final FeatherCore plugin) throws ModuleSetupException {
        for (final var moduleName : enableOrder) {
            final var module = this.moduleConfigs.get(moduleName);
            final var status = module.instance.onEnable(plugin);

            if (module.mandatory && status != ModuleEnableStatus.SUCCESS) {
                throw new ModuleSetupException("Mandatory module '" + moduleName + "' failed to enable");
            }
        }
    }

    private void disableModules(@NotNull final IFeatherLoggger logger) {
        for (int index = this.enableOrder.size() - 1; index >= 0; --index) {
            this.modules.get(this.enableOrder.get(index)).onDisable(logger);
        }
    }

    private static void assertEntryNotNull(final Object object, @NotNull final String tag)
            throws ModuleSetupException {
        if (object == null) {
            throw new ModuleSetupException(
                    tag + " is missing from " + FeatherModulesManager.DEPENDENCY_GRAPH_BUILDER_FILE_NAME);
        }
    }

    private static JSONObject loadJSON(@NotNull final FeatherCore plugin, @NotNull final String fileName)
            throws ModuleSetupException {
        JSONObject jsonObject = null;
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                final JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(new InputStreamReader(inputStream));
            } else {
                throw new ModuleSetupException(fileName
                        + " not found in plugin resources. That's weird O.o, please contact the developer");
            }
        } catch (IOException | ParseException e) {
            throw new ModuleSetupException(
                    "Error on parsing dependency graph resource -> cause: " + StringUtils.exceptionToStr(e));
        }
        if (jsonObject == null) {
            throw new ModuleSetupException("Dependencies setup failed");
        }
        return jsonObject;
    }
}
