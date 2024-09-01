package mc.owls.valley.net.feathercore.log;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class FeatherLogger {
    public static final String PLUGIN_TAG = "&8[&eFeatherCore&8]&r ";

    private final ConsoleCommandSender console;

    public FeatherLogger(final JavaPlugin plugin) {
        this.console = plugin.getServer().getConsoleSender();
    }

    public void info(final String message) {
        sendMessage("&3" + message);
    }

    public void success(final String message) {
        sendMessage("&a" + message);
    }

    public void warn(final String message) {
        sendMessage("&e" + message);
    }

    public void error(final String message) {
        sendMessage("&4" + message);
    }

    private void sendMessage(final String message) {
        this.console.sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN_TAG + message));
    }
}
