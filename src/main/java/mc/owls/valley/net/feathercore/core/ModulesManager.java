package mc.owls.valley.net.feathercore.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import mc.owls.valley.net.feathercore.api.common.Cache;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;

import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.configuration.components.bukkit.BukkitConfigFile;

public class ModulesManager {

    private static class ModuleConfig {
        public Set<String> dependencies = new HashSet<>();
        public FeatherModule instance = null;
        public boolean mandatory = false;
        public List<String> listeners = new ArrayList<>();
        public List<Pair<String, String>> commands = new ArrayList<>();
    }

    private Map<String, FeatherModule> modules = new HashMap<>();
    private Set<String> enabledModules = new HashSet<>();
    private List<String> enableOrder = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T extends FeatherModule> T getModule(final String name) {
        return (T) this.modules.get(name);
    }

    public boolean isModuleEnabled(final String name) {
        return this.enabledModules.contains(name);
    }

    public void onEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final var modules = loadModules(core);
        this.enableOrder = computeEnableOrder(modules);

        this.modules = modules.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().instance));

        enableModules(core, this.enableOrder, modules);

        for (final var module : modules.keySet()) {
            if (!this.enabledModules.contains(module)) {
                this.modules.remove(module);
                this.enableOrder.remove(module);
            }
        }
    }

    public void onDisable(final IFeatherLogger logger) {
        disableModules(logger);
    }

    private Map<String, ModuleConfig> loadModules(final IFeatherCoreProvider core) throws FeatherSetupException {
        final Map<String, ModuleConfig> modules = new HashMap<>();

        final var modulesConfig = YamlUtils.loadYaml(core.getPlugin(), FeatherCore.FEATHER_CORE_YML)
                .getConfigurationSection("modules");

        final var plugin = core.getPlugin();
        final var pluginClass = plugin.getClass();

        for (final var moduleName : modulesConfig.getKeys(false)) {
            final var moduleConfig = modulesConfig.getConfigurationSection(moduleName);

            // 1. create module config
            final var module = new ModuleConfig();
            modules.put(moduleName, module);

            // 2. create module instance
            final var moduleClass = moduleConfig.getString("class");
            try {
                final var clazz = Class.forName(moduleClass);
                module.instance = (FeatherModule) clazz.getConstructor(String.class).newInstance(moduleName);
            } catch (final Exception e) {
                throw new FeatherSetupException("Could not generate instance of class " + moduleClass + "\nReason: "
                        + StringUtils.exceptionToStr(e));
            }

            // 3. set mandatory
            module.mandatory = moduleConfig.getBoolean("mandatory");

            // 4. set listeners
            module.listeners = moduleConfig.getStringList("listeners");

            // 5. set commands
            module.commands = new ArrayList<>();
            final var commandsConfig = moduleConfig.getConfigurationSection("commands");
            if (commandsConfig != null) {
                for (final var commandName : commandsConfig.getKeys(false)) {
                    module.commands.add(Pair.of(commandName, commandsConfig.getString(commandName)));
                }
            }

            // 6. set dependencies
            for (final var dependency : moduleConfig.getStringList("dependencies")) {
                module.dependencies.add(dependency);
            }

            // 7. set module cache
            final var cacheFieldName = moduleConfig.getString("cache-field");

            if (cacheFieldName.isEmpty()) {
                throw new FeatherSetupException(
                        "Missing literal on '" + moduleName + "', please contact the developer");
            }

            try {
                final var field = pluginClass.getDeclaredField(cacheFieldName);
                field.setAccessible(true);
                field.set(plugin, Cache.of(() -> this.getModule(moduleName)));
            } catch (final Exception e) {
                throw new FeatherSetupException(
                        "Could not setup config connection {" + cacheFieldName + " -> " + moduleName
                                + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        // check if dependencies are actual modules
        for (final var moduleEntry : modules.entrySet()) {
            final var moduleName = moduleEntry.getKey();
            final var moduleData = moduleEntry.getValue();

            for (final var dependency : moduleData.dependencies) {
                if (modules.get(dependency) == null) {
                    throw new FeatherSetupException("Dependency '" + dependency + "' of module '" + moduleName
                            + "' does not name any valid module");
                }
            }
        }

        return modules;
    }

    private List<String> computeEnableOrder(final Map<String, ModuleConfig> moduleConfigs)
            throws FeatherSetupException {
        final List<String> order = new ArrayList<>();

        // 1. build the graph and compute in-degrees
        final Map<String, HashSet<String>> graph = new HashMap<>();
        final Map<String, Integer> inDegrees = new HashMap<>();

        for (final var moduleName : moduleConfigs.keySet()) {
            inDegrees.put(moduleName, 0);
            graph.put(moduleName, new HashSet<>());
        }

        for (final var entry : moduleConfigs.entrySet()) {
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
            order.add(moduleName);

            for (final var neighhbor : graph.get(moduleName)) {
                inDegrees.put(neighhbor, inDegrees.get(neighhbor) - 1);
                if (inDegrees.get(neighhbor) == 0) {
                    queue.add(neighhbor);
                }
            }
        }

        // check for cycles
        if (order.size() != moduleConfigs.size()) {
            throw new FeatherSetupException(
                    "There is a cycle in the dependency graph of modules configuration. This is weird O.o, please contact the developer");
        }

        return order;
    }

    private void enableModules(final IFeatherCoreProvider core, final List<String> enableOrder,
            final Map<String, ModuleConfig> moduleConfigs) throws FeatherSetupException {
        final IConfigFile modulesEnabledConfig = new BukkitConfigFile(core.getPlugin(), "modules.yml");

        for (final var moduleName : enableOrder) {
            // try to enable the module
            final var module = moduleConfigs.get(moduleName);
            final var configEnabled = modulesEnabledConfig.getBoolean(moduleName, true);

            if (!configEnabled && !module.mandatory) {
                continue;
            }

            module.instance.onEnable(core);

            this.enabledModules.add(moduleName);

            // register commands
            final var plugin = core.getPlugin();
            for (final var command : module.commands) {
                final var commandName = command.first;
                final var commandClass = command.second;

                try {
                    final var clazz = Class.forName(commandClass);
                    final var constructor = clazz.getConstructor();
                    final var method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                    method.setAccessible(true);

                    final var instance = (FeatherCommand<?>) constructor.newInstance();
                    method.invoke(instance, core);

                    final var cmd = plugin.getCommand(commandName);
                    cmd.setExecutor(instance);
                    cmd.setTabCompleter(instance);
                } catch (final Exception e) {
                    throw new FeatherSetupException(
                            "Could not setup command " + commandName + "\nReason: " + StringUtils.exceptionToStr(e));
                }
            }

            // register listeners
            final var pluginManager = plugin.getServer().getPluginManager();
            for (final var listenerClass : module.listeners) {
                try {
                    final var clazz = Class.forName(listenerClass);
                    final var constructor = clazz.getConstructor();
                    final var method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                    method.setAccessible(true);

                    final var instance = (IFeatherListener) constructor.newInstance();
                    method.invoke(instance, core);

                    pluginManager.registerEvents(instance, plugin);
                } catch (final Exception e) {
                    throw new FeatherSetupException(
                            "Could not setup listener " + listenerClass + "\nReason: " + StringUtils.exceptionToStr(e));
                }
            }

        }
    }

    private void disableModules(final IFeatherLogger logger) {
        for (int index = this.enableOrder.size() - 1; index >= 0; --index) {
            this.modules.get(this.enableOrder.get(index)).onDisable(logger);
        }
    }

}
