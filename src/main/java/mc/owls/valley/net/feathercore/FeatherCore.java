package mc.owls.valley.net.feathercore;

import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

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

        String connectionString = getConfig().getString("mongodb.uri");

        // Create a MongoDB client
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            // Access the "admin" database
            MongoDatabase database = mongoClient.getDatabase("admin");

            // Send a ping command to check connectivity
            Document command = new Document("ping", 1);
            Document result = database.runCommand(command);

            this.logger.info("Ping Result: " + result.toJson());

            // List all databases on the server (optional)
            MongoIterable<String> databases = mongoClient.listDatabaseNames();
            this.logger.info("Available Databases:");
            for (String dbName : databases) {
                this.logger.info(" - " + dbName);
            }

            this.logger.info("MongoDB connection is successful!");

        } catch (Exception e) {
            this.logger.error("An error occurred while connecting to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        messageConsole("&cGoodbye, PaperMC!");
    }

    public FeatherLogger getFeatherLogger() {
        return this.logger;
    }
}
