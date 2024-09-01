package mc.owls.valley.net.feathercore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoDBHandler;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import mc.owls.valley.net.feathercore.players.data.management.PlayersDataManager;
import mc.owls.valley.net.feathercore.utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;

public class FeatherCore extends JavaPlugin {
    private static FeatherLogger Logger = null;
    private static MongoDBHandler MongoDB = null;
    private static PlayersDataManager PlayersDataManager = null;

    @Override
    public void onEnable() {
        displayLogo();

        saveDefaultConfig();

        setupLoggger();
        setupMongoDB();

        FeatherCore.PlayersDataManager = new PlayersDataManager(this);

        FeatherCore.Logger.success("Setup finished successfully&8!");
    }

    @Override
    public void onDisable() {
        displayLogo();
        FeatherCore.Logger.info("&cGoodbye&8!");
    }

    public static FeatherLogger GetFeatherLogger() {
        return FeatherCore.Logger;
    }

    public PlayersDataManager getPlayersDataManager() {
        return FeatherCore.PlayersDataManager;
    }

    public PlayersDAO getPlayersDAO() {
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
        FeatherCore.Logger.success("MongoDB setup finished successfully!");
    }

    private void displayLogo() {
        final var fColor = ChatColor.YELLOW;
        final var cColor = ChatColor.GOLD;
        final String[] logo = new String[] {
                "",
                fColor + " ░░░░░" + cColor + "   ░░░░",
                fColor + " ░░   " + cColor + " ░░    ░░" + fColor + "  Feather" + cColor + "Core" + ChatColor.AQUA
                        + " v0.1.2",
                fColor + " ░░░░ " + cColor + " ░░      " + ChatColor.GRAY + "  Running on "
                        + this.getServerType() + " " + this.getServer().getVersion(),
                fColor + " ░░   " + cColor + " ░░    ░░" + ChatColor.GRAY + "  Author" + ChatColor.DARK_GRAY + ":"
                        + fColor + " DefaultyBuf",
                fColor + " ░░   " + cColor + "   ░░░░",
                ""
        };
        final var console = this.getServer().getConsoleSender();
        for (var line : logo) {
            console.sendMessage(ChatUtils.translateColors(line));
        }
    }

    private String getServerType() {
        String serverName = null;

        // Check for Paper
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            serverName = "Paper";
        } catch (ClassNotFoundException e) {
        }

        // Check for Spigot
        if (serverName == null) {
            try {

                Class.forName("org.spigotmc.SpigotConfig");
                serverName = "Spigot";
            } catch (ClassNotFoundException e) {
            }
        }

        // If neither Spigot nor Paper, it's Bukkit or an unknown server type
        if (serverName == null) {
            serverName = "Bukkit";
        }

        return serverName;
    }

}
