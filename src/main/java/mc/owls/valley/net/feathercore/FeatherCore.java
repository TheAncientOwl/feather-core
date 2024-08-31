package mc.owls.valley.net.feathercore;

import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class FeatherCore extends JavaPlugin {

    public void messageConsole(final String message) {
        getServer().getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eFeatherCore&8]&r " + message));
    }

    @Override
    public void onEnable() {
        messageConsole("&2Hello PaperMC!");
    }

    @Override
    public void onDisable() {
        messageConsole("&cGoodbye, PaperMC!");
    }
}
