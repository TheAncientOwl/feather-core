package mc.owls.valley.net.feathercore.core.modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class CommandsRegistrator extends FeatherModule {

    public CommandsRegistrator(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();

        final FileConfiguration config = YamlUtils.loadYaml(plugin, FeatherCore.PLUGIN_YML);
        final ConfigurationSection commands = config.getConfigurationSection("commands");

        for (final String commandName : commands.getKeys(false)) {
            final String className = commands.getConfigurationSection(commandName).getString("class");

            try {
                final Class<?> clazz = Class.forName(className);
                final Constructor<?> constructor = clazz.getConstructor();
                final Method method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                method.setAccessible(true);

                final IFeatherCommand instance = (IFeatherCommand) constructor.newInstance();
                method.invoke(instance, core);

                final PluginCommand command = plugin.getCommand(commandName);
                command.setExecutor(instance);
                command.setTabCompleter(instance);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | SecurityException
                    | InstantiationException
                    | InvocationTargetException e) {
                throw new FeatherSetupException(
                        "Could not setup command " + commandName + "\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
