/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LogoManager.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Module responsible for sending plugin logo to console
 */

package dev.defaultybuf.feathercore.modules.logo.components;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.exceptions.FeatherSetupException;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feather.toolkit.util.parsing.YamlUtils;
import dev.defaultybuf.feathercore.modules.logo.interfaces.ILogoManager;

public class LogoManager extends FeatherModule implements ILogoManager {
    public LogoManager(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
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
        final var plugin = getPlugin();
        final var server = plugin.getServer();

        final String[] logo = new String[] {
                "",
                " &e&l░░░░░ &6&l  ░░░░",
                " &e&l░░    &6&l░░    ░░  &eFeather&6Core &bv"
                        + YamlUtils.loadYaml(plugin, "plugin.yml").getString(
                                "version"),
                " &e&l░░░░  &6&l░░        &7&oRunning on " + getServerType() + " "
                        + server.getVersion(),
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
