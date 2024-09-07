package mc.owls.valley.net.feathercore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {
    public static String translateColors(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(final CommandSender commandSender, final String message) {
        commandSender.sendMessage(translateColors(message));
    }

    public static void sendMessage(final Player player, final String message) {
        player.sendMessage(translateColors(message));
    }
}
