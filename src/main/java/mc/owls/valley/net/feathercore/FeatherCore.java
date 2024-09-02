package mc.owls.valley.net.feathercore;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoDBHandler;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import mc.owls.valley.net.feathercore.players.data.management.PlayersDataManager;
import mc.owls.valley.net.feathercore.utils.LogoManager;

public class FeatherCore extends JavaPlugin {
    private FeatherLogger featherLogger = null;
    private MongoDBHandler mongoDB = null;
    private PlayersDataManager playersDataManager = null;

    @Override
    public void onEnable() {
        LogoManager.logLogo(this.getServer());

        saveDefaultConfig();

        this.featherLogger = FeatherLogger.setup(this);
        this.mongoDB = MongoDBHandler.setup(getConfig(), this);
        this.playersDataManager = PlayersDataManager.setup(this);

        this.featherLogger.success("Setup finished successfully&8!");
    }

    @Override
    public void onDisable() {
        this.featherLogger.info("Saving players data");
        this.playersDataManager.savePlayersData();

        LogoManager.logLogo(this.getServer());
        this.featherLogger.info("&cGoodbye&8!");
    }

    public FeatherLogger getFeatherLogger() {
        return this.featherLogger;
    }

    public PlayersDataManager getPlayersDataManager() {
        return this.playersDataManager;
    }

    public PlayersDAO getPlayersDAO() {
        return this.mongoDB.getDAO(PlayersDAO.class);
    }
}
