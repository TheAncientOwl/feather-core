package mc.owls.valley.net.feathercore.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import mc.owls.valley.net.feathercore.api.common.Cache;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.configuration.components.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigFile;

public class ModulesManager {

    private static class ModuleConfig {
        public Set<String> dependencies = null;
        public FeatherModule instance = null;
        public boolean mandatory = false;
        public List<String> listeners = null;
        public List<Pair<String, String>> commands = null;
    }

    private static class InitializationData {
        public Map<String, ModuleConfig> moduleConfigs = new HashMap<>();
        public Set<String> enabledModules = new HashSet<>();
    }

    private InitializationData init = new InitializationData();

    private Map<String, FeatherModule> modules = new HashMap<>();
    private List<String> enableOrder = new ArrayList<>();
    private IFeatherCoreProvider core = null;

    @SuppressWarnings("unchecked")
    public <T extends FeatherModule> T getModule(final String name) {
        return (T) this.modules.get(name);
    }

    /**
     * This method should be used only in FeatherModule.onEnable(core)
     * 
     * @note After ModulesManager.onEnable(core) was finished, this method will
     *       always return false
     * @param name of the module
     */
    public boolean isModuleEnabled(final String name) {
        return this.init.enabledModules.contains(name);
    }

    public void onEnable(final IFeatherCoreProvider core) throws FeatherSetupException, ModuleNotEnabledException {
        this.init.moduleConfigs.clear();
        this.init.enabledModules.clear();
        this.modules.clear();
        this.enableOrder.clear();
        this.core = core;

        loadModules(core);
        computeEnableOrder();

        this.init.moduleConfigs.forEach((moduleName, moduleConfig) -> {
            this.modules.put(moduleName, moduleConfig.instance);
        });

        enableModules(core);
        doPostEnableCleanup();
    }

    public void onDisable(final IFeatherLogger logger) {
        disableModules(logger);
    }

    private void disablePlugin(final String reason) {
        core.getFeatherLogger().error(reason);

        final var plugin = this.core.getPlugin();
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    /**
     * Load modules configs from file
     * 
     * @param core
     * @throws FeatherSetupException
     */
    void loadModules(final IFeatherCoreProvider core) throws FeatherSetupException {
        final var config = YamlUtils.loadYaml(core.getPlugin(), FeatherCore.FEATHER_CORE_YML)
                .getConfigurationSection("modules");

        final var plugin = core.getPlugin();
        final var pluginClass = plugin.getClass();

        for (final var moduleName : config.getKeys(false)) {
            final var moduleConfig = config.getConfigurationSection(moduleName);

            // 1. create module config
            final var module = new ModuleConfig();
            this.init.moduleConfigs.put(moduleName, module);

            // 2. create module instance
            final var moduleClass = moduleConfig.getString("class");
            try {
                module.instance = (FeatherModule) Class.forName(moduleClass)
                        .getConstructor(String.class)
                        .newInstance(moduleName);
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
                commandsConfig.getKeys(false).forEach(commandName -> module.commands
                        .add(Pair.of(commandName, commandsConfig.getString(commandName))));
            }

            // 6. set dependencies
            module.dependencies = new HashSet<>(moduleConfig.getStringList("dependencies"));

            // 7. set core module cache
            final var cacheFieldName = moduleConfig.getString("cache-field");
            if (cacheFieldName.isEmpty()) {
                throw new FeatherSetupException(
                        "Missing literal on '" + moduleName + "', please contact the developer");
            }
            try {
                final var field = pluginClass.getDeclaredField(cacheFieldName);
                field.setAccessible(true);
                field.set(plugin, Cache.of(() -> {
                    final var mod = this.getModule(moduleName);

                    if (!this.init.enabledModules.contains(moduleName)) {
                        disablePlugin("Dependency module " + moduleName + " is not enabled in config.");
                    }

                    return mod;
                }));
            } catch (final Exception e) {
                throw new FeatherSetupException(
                        "Could not setup config connection {" + cacheFieldName + " -> " + moduleName
                                + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        // check if dependencies are actual modules
        for (final var moduleEntry : this.init.moduleConfigs.entrySet()) {
            final var moduleName = moduleEntry.getKey();
            final var moduleConfig = moduleEntry.getValue();

            for (final var dependency : moduleConfig.dependencies) {
                if (!this.init.moduleConfigs.containsKey(dependency)) {
                    throw new FeatherSetupException("Dependency '" + dependency + "' of module '" + moduleName
                            + "' does not name any valid module");
                }
            }
        }
    }

    /**
     * Self explanatory
     * 
     * @throws FeatherSetupException
     */
    void computeEnableOrder() throws FeatherSetupException {
        // 1. build the graph and compute in-degrees
        final var graph = new HashMap<String, HashSet<String>>();
        final var inDegrees = new HashMap<String, Integer>();

        for (final var moduleName : this.init.moduleConfigs.keySet()) {
            inDegrees.put(moduleName, 0);
            graph.put(moduleName, new HashSet<>());
        }

        for (final var entry : this.init.moduleConfigs.entrySet()) {
            final var moduleName = entry.getKey();
            for (final var dependency : entry.getValue().dependencies) {
                graph.get(dependency).add(moduleName);
                inDegrees.put(moduleName, inDegrees.get(moduleName) + 1);
            }
        }

        // 2. perform topological sort using Kahn's algorithm
        final Queue<String> queue = new ArrayDeque<>();
        for (final var entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            final var moduleName = queue.poll();
            this.enableOrder.add(moduleName);

            for (final var neighhbor : graph.get(moduleName)) {
                inDegrees.put(neighhbor, inDegrees.get(neighhbor) - 1);
                if (inDegrees.get(neighhbor) == 0) {
                    queue.add(neighhbor);
                }
            }
        }

        // check for cycles
        if (this.enableOrder.size() != this.init.moduleConfigs.size()) {
            throw new FeatherSetupException(
                    "There is a cycle in the dependency graph of modules configuration. This is weird O.o, please contact the developer");
        }
    }

    /**
     * Enable modules taking into account if the module was or not enabled in config
     * 
     * @param core
     * @throws FeatherSetupException
     */
    private void enableModules(final IFeatherCoreProvider core)
            throws FeatherSetupException, ModuleNotEnabledException {
        final IConfigFile modulesEnabledConfig = new BukkitConfigFile(core.getPlugin(), "modules.yml");

        final var plugin = core.getPlugin();
        final var pluginManager = plugin.getServer().getPluginManager();

        for (final var moduleName : enableOrder) {
            // try to enable the module
            final var module = this.init.moduleConfigs.get(moduleName);
            final var configEnabled = modulesEnabledConfig.getBoolean(moduleName, true);

            if (!configEnabled && !module.mandatory) {
                continue;
            }

            // check if all dependencies are enabled
            for (final var dependency : module.dependencies) {
                if (!this.init.enabledModules.contains(dependency)) {
                    throw new FeatherSetupException("Module " + moduleName + " failed to enable because dependency "
                            + dependency + " is not enabled in config.");
                }
            }

            module.instance.onEnable(core);

            this.init.enabledModules.add(moduleName);

            // register commands
            for (final var command : module.commands) {
                final var commandName = command.first;
                final var commandClass = command.second;

                try {
                    final var clazz = Class.forName(commandClass);
                    final var method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                    final var cmdInstance = (FeatherCommand<?>) clazz.getConstructor().newInstance();
                    final var cmd = plugin.getCommand(commandName);

                    method.setAccessible(true);
                    method.invoke(cmdInstance, core);

                    cmd.setExecutor(cmdInstance);
                    cmd.setTabCompleter(cmdInstance);
                } catch (final Exception e) {
                    throw new FeatherSetupException(
                            "Could not setup command " + commandName + "\nReason: " + StringUtils.exceptionToStr(e));
                }
            }

            // register listeners
            for (final var listenerClass : module.listeners) {
                try {
                    final var clazz = Class.forName(listenerClass);
                    final var method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                    final var listenerInstance = (IFeatherListener) clazz.getConstructor().newInstance();

                    method.setAccessible(true);
                    method.invoke(listenerInstance, core);

                    pluginManager.registerEvents(listenerInstance, plugin);
                } catch (final Exception e) {
                    throw new FeatherSetupException(
                            "Could not setup listener " + listenerClass + "\nReason: " + StringUtils.exceptionToStr(e));
                }
            }

        }
    }

    private void doPostEnableCleanup() {
        for (final var module : modules.keySet()) {
            if (!this.init.enabledModules.contains(module)) {
                this.modules.remove(module);
                this.enableOrder.remove(module);
            }
        }

        this.init.moduleConfigs.clear();
    }

    private void disableModules(final IFeatherLogger logger) {
        for (int index = this.enableOrder.size() - 1; index >= 0; --index) {
            if (this.init.enabledModules.contains(this.enableOrder.get(index))) {
                this.modules.get(this.enableOrder.get(index)).onDisable(logger);
            }
        }
    }

}
