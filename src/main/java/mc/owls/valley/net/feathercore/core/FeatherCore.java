package mc.owls.valley.net.feathercore.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.core.api.IFeatherCore;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import mc.owls.valley.net.feathercore.logging.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IMongoManager;
import mc.owls.valley.net.feathercore.modules.data.players.manager.api.IPlayersDataManager;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModulesManager;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import mc.owls.valley.net.feathercore.utils.LogoManager;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import mc.owls.valley.net.feathercore.utils.TimeUtils;

public class FeatherCore extends JavaPlugin implements IFeatherCore {
    private FeatherLogger featherLogger = null;
    private FeatherModulesManager modulesManager = new FeatherModulesManager();

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();

        LogoManager.logLogo(getServer());

        saveDefaultConfig();

        this.featherLogger = FeatherLogger.setup(this);

        try {
            this.modulesManager.onEnable(this);

            final var enableFinishTime = System.currentTimeMillis();
            this.featherLogger.info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatDuration(enableStartTime, enableFinishTime) + "&8)");
        } catch (final ModuleSetupException e) {
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
    public IMongoManager getMongoManager() {
        return this.modulesManager.getModule("MongoManager");
    }

    @Override
    public IPlayersDataManager getPlayersDataManager() {
        return this.modulesManager.getModule("PlayersDataManagement");
    }
}
