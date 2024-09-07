package mc.owls.valley.net.feathercore.core.common;

import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;

import mc.owls.valley.net.feathercore.api.IFeatherConfigurationManager;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.configuration.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.YamlUtils;

public class ConfigurationManager extends FeatherModule implements IFeatherConfigurationManager {
    private IConfigFile dataConfigFile = null;
    private IConfigFile economyConfigFile = null;
    private IConfigFile messagesConfigFile = null;

    public ConfigurationManager(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {
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
        this.dataConfigFile.saveConfig();
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
