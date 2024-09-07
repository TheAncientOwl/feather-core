package mc.owls.valley.net.feathercore.core.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.YamlUtils;

public class CommandsRegistrator extends FeatherModule {
    private static final String COMMANDS_FILE_NAME = "plugin.yml";

    public CommandsRegistrator(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {

        final FileConfiguration config = YamlUtils.loadYaml(plugin,
                CommandsRegistrator.COMMANDS_FILE_NAME);
        final ConfigurationSection commands = config.getConfigurationSection("commands");

        for (final String commandName : commands.getKeys(false)) {
            final String className = commands.getConfigurationSection(commandName).getString("class");

            try {
                final Class<?> clazz = Class.forName(className);
                final Constructor<?> constructor = clazz.getConstructor();
                final Method method = clazz.getMethod("onCreate", FeatherCore.class);
                method.setAccessible(true);

                final IFeatherCommand instance = (IFeatherCommand) constructor.newInstance();
                method.invoke(instance, plugin);

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
