package mc.owls.valley.net.feathercore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoDBHandler;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import mc.owls.valley.net.feathercore.players.data.management.PlayersDataManager;
import mc.owls.valley.net.feathercore.utils.LogoManager;

public class FeatherCore extends JavaPlugin {
    private static FeatherLogger Logger = null;
    private MongoDBHandler mongoDB = null;
    private PlayersDataManager playersDataManager = null;

    @Override
    public void onEnable() {
        LogoManager.logLogo(this.getServer());

        saveDefaultConfig();

        setupLoggger();

        this.mongoDB = MongoDBHandler.setup(getConfig(), this);

        this.playersDataManager = new PlayersDataManager(this);

        FeatherCore.Logger.success("Setup finished successfully&8!");
    }

    @Override
    public void onDisable() {
        FeatherCore.GetFeatherLogger().info("Saving players data");
        this.playersDataManager.savePlayersData();

        LogoManager.logLogo(this.getServer());
        FeatherCore.Logger.info("&cGoodbye&8!");
    }

    public static FeatherLogger GetFeatherLogger() {
        return FeatherCore.Logger;
    }

    public PlayersDataManager getPlayersDataManager() {
        return this.playersDataManager;
    }

    public PlayersDAO getPlayersDAO() {
        return this.mongoDB.getDAO(PlayersDAO.class);
    }

    private void setupLoggger() {
        FeatherCore.Logger = new FeatherLogger(this);
    }
}
