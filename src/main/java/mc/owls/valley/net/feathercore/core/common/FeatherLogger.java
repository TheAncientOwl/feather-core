package mc.owls.valley.net.feathercore.core.common;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;

public class FeatherLogger implements IFeatherLogger {
    public static final String PLUGIN_TAG = "&8[&eFeather&6Core&8]&r ";

    private final ConsoleCommandSender console;

    public static FeatherLogger setup(final JavaPlugin plugin) {
        return new FeatherLogger(plugin);
    }

    public FeatherLogger(final JavaPlugin plugin) {
        this.console = plugin.getServer().getConsoleSender();
    }

    public void info(final String message) {
        sendMessage("&8» &b" + message);
    }

    public void warn(final String message) {
        sendMessage("&8» &e" + message);
    }

    public void error(final String message) {
        sendMessage("&8» &c" + message);
    }

    public void debug(final String message) {
        sendMessage("&8» &a" + message);
    }

    private void sendMessage(final String message) {
        this.console.sendMessage(StringUtils.translateColors(PLUGIN_TAG + message));
    }
}
