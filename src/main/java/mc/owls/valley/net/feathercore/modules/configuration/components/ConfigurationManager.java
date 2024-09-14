package mc.owls.valley.net.feathercore.modules.configuration.components;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;

import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.configuration.components.bukkit.BukkitConfigFile;

public class ConfigurationManager extends FeatherModule implements IConfigurationManager {
    private IConfigFile dataConfigFile = null;
    private IConfigFile economyConfigFile = null;
    private IConfigFile messagesConfigFile = null;
    private IConfigFile pvpConfigFile = null;

    public ConfigurationManager(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();

        final var configs = YamlUtils.loadYaml(plugin, FeatherCore.FEATHER_CORE_YML).getConfigurationSection("configs");

        for (final var config : configs.getKeys(false)) {
            final String fieldName = config;
            final String configName = configs.getString(config);

            try {
                final var clazz = this.getClass();
                final var field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, new BukkitConfigFile(plugin, configName));
            } catch (final Exception e) {
                throw new FeatherSetupException("Could not setup config connection {" + fieldName + " -> " + configName
                        + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }
    }

    @Override
    protected void onModuleDisable() {
        try {
            this.dataConfigFile.saveConfig();
            this.economyConfigFile.saveConfig();
            this.messagesConfigFile.saveConfig();
            this.pvpConfigFile.saveConfig();
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

    @Override
    public IConfigFile getPvPConfigFile() {
        return this.pvpConfigFile;
    }

}
