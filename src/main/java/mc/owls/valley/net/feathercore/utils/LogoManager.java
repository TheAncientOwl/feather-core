package mc.owls.valley.net.feathercore.utils;

import org.bukkit.Server;

import net.md_5.bungee.api.ChatColor;

public class LogoManager {
    public static void logLogo(final Server server) {
        final var fColor = ChatColor.YELLOW;
        final var cColor = ChatColor.GOLD;
        final String[] logo = new String[] {
                "",
                fColor + " ░░░░░" + cColor + "   ░░░░",
                fColor + " ░░   " + cColor + " ░░    ░░" + fColor + "  Feather" + cColor + "Core" + ChatColor.AQUA
                        + " v0.1.2",
                fColor + " ░░░░ " + cColor + " ░░      " + ChatColor.GRAY + "  Running on "
                        + getServerType() + " " + server.getVersion(),
                fColor + " ░░   " + cColor + " ░░    ░░" + ChatColor.GRAY + "  Author" + ChatColor.DARK_GRAY + ":"
                        + fColor + " DefaultyBuf",
                fColor + " ░░   " + cColor + "   ░░░░",
                ""
        };

        final var console = server.getConsoleSender();

        for (var line : logo) {
            console.sendMessage(ChatUtils.translateColors(line));
        }
    }

    private static String getServerType() {
        String serverType = null;

        // Check for Paper
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            serverType = "Paper";
        } catch (ClassNotFoundException e) {
        }

        // Check for Spigot
        if (serverType == null) {
            try {

                Class.forName("org.spigotmc.SpigotConfig");
                serverType = "Spigot";
            } catch (ClassNotFoundException e) {
            }
        }

        // If neither Spigot nor Paper, it's Bukkit or an unknown server type
        if (serverType == null) {
            serverType = "Bukkit";
        }

        return serverType;
    }
}
