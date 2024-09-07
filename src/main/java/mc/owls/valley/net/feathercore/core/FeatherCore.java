package mc.owls.valley.net.feathercore.core;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.IFeatherConfigurationManager;
import mc.owls.valley.net.feathercore.api.IFeatherCore;
import mc.owls.valley.net.feathercore.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.api.data.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.data.mongo.IMongoDB;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.common.FeatherLogger;
import mc.owls.valley.net.feathercore.core.common.ModulesManager;
import mc.owls.valley.net.feathercore.modules.economy.vault.VaultModule;
import mc.owls.valley.net.feathercore.utils.LogoManager;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.TimeUtils;
import net.milkbowl.vault.economy.Economy;

public class FeatherCore extends JavaPlugin implements IFeatherCore {
    public static final String PLUGIN_YML = "plugin.yml";

    private IFeatherLoggger featherLogger = null;
    private ModulesManager modulesManager = new ModulesManager();

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();

        LogoManager.logLogo(getServer());

        this.featherLogger = FeatherLogger.setup(this);

        try {
            this.modulesManager.onEnable(this);

            final var enableFinishTime = System.currentTimeMillis();
            this.featherLogger.info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatDuration(enableStartTime, enableFinishTime) + "&8)");
        } catch (final FeatherSetupException e) {
            this.featherLogger.error(StringUtils.exceptionToStr(e));

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        LogoManager.logLogo(getServer());

        this.modulesManager.onDisable(this.featherLogger);

        this.featherLogger.info("Goodbye&8!");
    }

    @Override
    public IFeatherLoggger getFeatherLogger() {
        return this.featherLogger;
    }

    @Override
    public IMongoDB getMongoDB() {
        return this.modulesManager.getModule("MongoModule");
    }

    @Override
    public IPlayersDataManager getPlayersDataManager() {
        return this.modulesManager.getModule("PlayersDataModule");
    }

    @Override
    public IFeatherConfigurationManager getConfigurationManager() {
        return this.modulesManager.getModule("ConfigurationManager");
    }

    @Override
    public Economy getEconomy() {
        final VaultModule vault = this.modulesManager.getModule("VaultModule");
        return vault.getEconomy();
    }

}
