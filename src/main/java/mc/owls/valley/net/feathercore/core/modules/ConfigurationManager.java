package mc.owls.valley.net.feathercore.core.modules;

import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.core.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.configuration.bukkit.BukkitConfigFile;

public class ConfigurationManager extends FeatherModule implements IConfigurationManager {
    private IConfigFile dataConfigFile = null;
    private IConfigFile economyConfigFile = null;
    private IConfigFile messagesConfigFile = null;

    public ConfigurationManager(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();

        final FileConfiguration pluginConfig = YamlUtils.loadYaml(plugin, FeatherCore.PLUGIN_YML);
        final var configs = pluginConfig.getMapList("feathercore.configs");

        for (final var config : configs) {
            final String fieldName = (String) config.get("field");
            final String configName = (String) config.get("config");

            try {
                final Class<?> clazz = this.getClass();
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, new BukkitConfigFile(plugin, configName));
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
                throw new FeatherSetupException("Could not setup config connection {" + fieldName + " -> " + configName
                        + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
        try {
            this.dataConfigFile.saveConfig();
            this.economyConfigFile.saveConfig();
            this.messagesConfigFile.saveConfig();
        } catch (final IOException e) {
        }
    }

    @Override
    public IConfigFile getDataConfiguration() {
        return this.dataConfigFile;
    }

    @Override
    public IConfigFile getEconomyConfigFile() {
        return this.economyConfigFile;
    }

    @Override
    public IConfigFile getMessagesConfigFile() {
        return this.messagesConfigFile;
    }

}
