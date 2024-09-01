package mc.owls.valley.net.feathercore.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {
    public static String translateColors(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
