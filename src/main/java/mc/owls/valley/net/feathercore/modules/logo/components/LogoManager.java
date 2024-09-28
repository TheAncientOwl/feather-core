package mc.owls.valley.net.feathercore.modules.logo.components;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public class LogoManager extends FeatherModule {
    private JavaPlugin plugin = null;

    public LogoManager(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.plugin = core.getPlugin();
        this.sendLogoMessage();
    }

    @Override
    protected void onModuleDisable() {
        try {
            this.sendLogoMessage();
        } catch (final Exception e) {
        }
    }

    private void sendLogoMessage() throws FeatherSetupException {
        final var server = this.plugin.getServer();

        final String[] logo = new String[] {
                "",
                " &e&l░░░░░ &6&l  ░░░░",
                " &e&l░░    &6&l░░    ░░  &eFeather&6Core &bv"
                        + YamlUtils.loadYaml(this.plugin, "plugin.yml").getString(
                                "version"),
                " &e&l░░░░  &6&l░░        &7&oRunning on " + getServerType() + " " + server.getVersion(),
                " &e&l░░    &6&l░░    ░░  &7&oAuthor: DefaultyBuf",
                " &e&l░░    &6&l  ░░░░",
                "",
        };

        final var console = server.getConsoleSender();
        for (final var line : logo) {
            console.sendMessage(StringUtils.translateColors(line));
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
