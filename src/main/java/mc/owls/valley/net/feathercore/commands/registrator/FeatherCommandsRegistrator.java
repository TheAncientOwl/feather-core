package mc.owls.valley.net.feathercore.commands.registrator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import mc.owls.valley.net.feathercore.commands.api.FeatherCommand;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.YamlUtils;

public class FeatherCommandsRegistrator extends FeatherModule {
    private static final String COMMANDS_FILE_NAME = "plugin.yml";

    public FeatherCommandsRegistrator(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws ModuleSetupException {

        final FileConfiguration config = YamlUtils.loadYaml(plugin,
                FeatherCommandsRegistrator.COMMANDS_FILE_NAME);
        final ConfigurationSection commands = config.getConfigurationSection("commands");

        for (final String command : commands.getKeys(false)) {
            final String className = commands.getConfigurationSection(command).getString("class");

            try {
                final Class<?> clazz = Class.forName(className);
                final Constructor<?> constructor = clazz.getConstructor();
                final Method method = clazz.getMethod("onCreate", FeatherCore.class);
                method.setAccessible(true);

                final FeatherCommand instance = (FeatherCommand) constructor.newInstance();
                method.invoke(instance, plugin);

                plugin.getCommand(command).setExecutor(instance);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | SecurityException
                    | InstantiationException
                    | InvocationTargetException e) {
                throw new ModuleSetupException(
                        "Could not setup command " + command + "\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
