package mc.owls.valley.net.feathercore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoDBHandler;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;

public class FeatherCore extends JavaPlugin {
    private static FeatherLogger Logger = null;
    private static MongoDBHandler MongoDB = null;

    @Override
    public void onEnable() {
        setupLoggger();
        FeatherCore.Logger.info("&2Hello PaperMC!");

        saveDefaultConfig();

        setupMongoDB();
    }

    @Override
    public void onDisable() {
        FeatherCore.Logger.info("&cGoodbye, PaperMC!");
    }

    public static FeatherLogger GetFeatherLogger() {
        return FeatherCore.Logger;
    }

    public static PlayersDAO GetPlayersDAO() {
        return FeatherCore.MongoDB.getDAO(PlayersDAO.class);
    }

    private void setupLoggger() {
        FeatherCore.Logger = new FeatherLogger(this);
    }

    private void setupMongoDB() {
        ConfigurationSection mongoConfig = getConfig().getConfigurationSection("mongodb");
        FeatherCore.MongoDB = new MongoDBHandler(
                mongoConfig.getString("uri"), mongoConfig.getString("dbname"),
                PlayerModel.class);
        if (!FeatherCore.MongoDB.connected()) {
            FeatherCore.Logger.error("Failed to setup MongoDB, shutting down the plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        FeatherCore.Logger.success("MongoDB setup successfully!");
    }

}
