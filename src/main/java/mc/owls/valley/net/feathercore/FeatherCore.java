package mc.owls.valley.net.feathercore;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.databases.mongodb.MongoManager;
import mc.owls.valley.net.feathercore.log.FeatherLogger;
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

        MongoManager mongoManager = new MongoManager(this);

        mongoManager.setupJAR();
    }

    @Override
    public void onDisable() {
        messageConsole("&cGoodbye, PaperMC!");
    }

    public FeatherLogger getFeatherLogger() {
        return this.logger;
    }
}
