/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerLogoutListener.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Kill player in combat on disconnect
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.pvp.manager.common.Message;
import mc.owls.valley.net.feathercore.modules.pvp.manager.components.PvPManager;

public class PlayerLogoutListener implements IFeatherListener {
    private PvPManager pvpManager = null;
    private LanguageManager lang = null;
    private IPropertyAccessor config = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getPvPManager();
        this.config = core.getPvPManager().getConfig();
        this.lang = core.getLanguageManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!this.pvpManager.isPlayerInCombat(player) || !this.config.getBoolean("on-logout.kill")
                || player.hasPermission("pvp.bypass.killonlogout")) {
            return;
        }

        this.pvpManager.removePlayerInCombat(player);
        player.setHealth(0);

        if (!this.config.getBoolean("on-logout.broadcast")) {
            return;
        }

        for (final var onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.lang.message(onlinePlayer, Message.LOGOUT, Pair.of(Placeholder.PLAYER, player.getName()));
        }
    }
}
