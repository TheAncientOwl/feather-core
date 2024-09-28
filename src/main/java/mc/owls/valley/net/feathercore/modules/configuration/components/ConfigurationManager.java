package mc.owls.valley.net.feathercore.modules.configuration.components;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.configuration.components.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigurationManager;

// TODO: Refactor to not load all the config files in the beginning
public class ConfigurationManager extends FeatherModule implements IConfigurationManager {
    private IFeatherLogger logger = null;
    private IConfigFile dataConfigFile = null;
    private IConfigFile economyConfigFile = null;
    private IConfigFile pvpConfigFile = null;
    private IConfigFile translationsConfigFile = null;
    private IConfigFile lootChestsConfigFile = null;

    public ConfigurationManager(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.logger = core.getFeatherLogger();

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
        saveConfigFile(this.dataConfigFile);
        saveConfigFile(this.economyConfigFile);
        saveConfigFile(this.pvpConfigFile);
        saveConfigFile(this.translationsConfigFile);
    }

    private void saveConfigFile(final IConfigFile config) {
        if (config == null) {
            return;
        }

        this.logger.info(config.getFileName() + " » saving&7...");
        try {
            config.saveConfig();
        } catch (final IOException e) {
            this.logger.error(config.getFileName() + " » save failed...\nReason: "
                    + StringUtils.exceptionToStr(e));
            return;
        }
        this.logger.info(config.getFileName() + " » saved&7.");

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
    public IConfigFile getPvPConfigFile() {
        return this.pvpConfigFile;
    }

    @Override
    public IConfigFile getTranslationsConfigFile() {
        return this.translationsConfigFile;
    }

    @Override
    public IConfigFile getLootChestsConfigFile() {
        return this.lootChestsConfigFile;
    }

}
