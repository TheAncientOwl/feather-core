package mc.owls.valley.net.feathercore;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Random;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoDBHandler;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayerDataDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerData;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import net.md_5.bungee.api.ChatColor;

public class FeatherCore extends JavaPlugin {
    private FeatherLogger logger = null;

    public void messageConsole(final String message) {
        getServer().getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eFeatherCore&8]&r " + message));
    }

    @Override
    public void onEnable() {
        messageConsole("&2Hello PaperMC!");

        logger = new FeatherLogger(this);

        saveDefaultConfig();

        ConfigurationSection mongo_cfg = getConfig().getConfigurationSection("mongodb");
        MongoDBHandler dbHandler = new MongoDBHandler(
                mongo_cfg.getString("uri"), mongo_cfg.getString("dbname"), PlayerData.class);

        PlayerData outPlayer = new PlayerData();
        outPlayer.uuid = UUID.randomUUID();
        outPlayer.username = "SomeUsername";
        outPlayer.balance = new Random().nextFloat() + 256;

        PlayerDataDAO playerDAO = new PlayerDataDAO(dbHandler.getDatastore());
        playerDAO.save(outPlayer);

        PlayerData inPlayer = playerDAO.get(outPlayer.uuid);
        if (inPlayer != null) {
            this.logger.info(
                    "PlayerData: " + inPlayer.uuid.toString() + " | " + inPlayer.username + " | " + inPlayer.balance);
        } else {
            this.logger.error("Cannot read data");
        }

        for (PlayerData data : playerDAO.findAll()) {
            this.logger.info(
                    "PlayerData: " + data.uuid.toString() + " | " + data.username + " | " + data.balance);

        }

        // String connectionString = getConfig().getString("mongodb.uri");

        // Create a MongoDB client
        // try (MongoClient mongoClient = MongoClients.create(connectionString)) {

        // // Access the "admin" database
        // MongoDatabase database = mongoClient.getDatabase("admin");

        // // Send a ping command to check connectivity
        // Document command = new Document("ping", 1);
        // Document result = database.runCommand(command);

        // this.logger.info("Ping Result: " + result.toJson());

        // // List all databases on the server (optional)
        // MongoIterable<String> databases = mongoClient.listDatabaseNames();
        // this.logger.info("Available Databases:");
        // for (String dbName : databases) {
        // this.logger.info(" - " + dbName);
        // }

        // this.logger.info("MongoDB connection is successful!");

        // } catch (Exception e) {
        // this.logger.error("An error occurred while connecting to MongoDB: " +
        // e.getMessage());
        // e.printStackTrace();
        // }
    }

    @Override
    public void onDisable() {
        messageConsole("&cGoodbye, PaperMC!");
    }

    public FeatherLogger getFeatherLogger() {
        return this.logger;
    }
}
