package mc.owls.valley.net.feathercore.core.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import mc.owls.valley.net.feathercore.api.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.YamlUtils;

public class ListenersRegistrator extends FeatherModule {
    private static final String EVENTS_FILE_NAME = "plugin.yml";

    public ListenersRegistrator(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {

        final FileConfiguration config = YamlUtils.loadYaml(plugin,
                ListenersRegistrator.EVENTS_FILE_NAME);
        final List<String> listeners = config.getStringList("feathercore.listeners");

        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        for (final String listenerClass : listeners) {
            try {
                final Class<?> clazz = Class.forName(listenerClass);
                final Constructor<?> constructor = clazz.getConstructor();
                final Method method = clazz.getMethod("onCreate", FeatherCore.class);
                method.setAccessible(true);

                final IFeatherListener instance = (IFeatherListener) constructor.newInstance();
                method.invoke(instance, plugin);

                pluginManager.registerEvents(instance, plugin);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | SecurityException
                    | InstantiationException
                    | InvocationTargetException e) {
                throw new FeatherSetupException(
                        "Could not setup listener " + listenerClass + "\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
