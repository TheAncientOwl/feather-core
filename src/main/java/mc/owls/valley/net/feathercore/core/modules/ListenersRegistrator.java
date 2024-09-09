package mc.owls.valley.net.feathercore.core.modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.core.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class ListenersRegistrator extends FeatherModule {

    public ListenersRegistrator(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();

        final FileConfiguration config = YamlUtils.loadYaml(plugin, FeatherCore.PLUGIN_YML);
        final List<String> listeners = config.getStringList("feathercore.listeners");

        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        for (final String listenerClass : listeners) {
            try {
                final Class<?> clazz = Class.forName(listenerClass);
                final Constructor<?> constructor = clazz.getConstructor();
                final Method method = clazz.getMethod("onCreate", IFeatherCoreProvider.class);
                method.setAccessible(true);

                final IFeatherListener instance = (IFeatherListener) constructor.newInstance();
                method.invoke(instance, core);

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
