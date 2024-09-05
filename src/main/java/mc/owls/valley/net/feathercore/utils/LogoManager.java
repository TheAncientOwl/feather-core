package mc.owls.valley.net.feathercore.utils;

import org.bukkit.Server;

public class LogoManager {
    public static void logLogo(final Server server) {
        final String[] logo = new String[] {
                "",
                " &e&l░░░░░ &6&l  ░░░░",
                " &e&l░░    &6&l░░    ░░  &eFeather&6Core &bv0.2.1",
                " &e&l░░░░  &6&l░░        &7&oRunning on " + getServerType() + " " + server.getVersion(),
                " &e&l░░    &6&l░░    ░░  &7&oAuthor: DefaultyBuf",
                " &e&l░░    &6&l  ░░░░",
                "",
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
