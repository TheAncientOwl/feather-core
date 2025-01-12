/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerLogoutListener.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Kill player in combat on disconnect
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PlayerLogoutListener extends FeatherListener {
    public PlayerLogoutListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!getInterface(IPvPManager.class).isPlayerInCombat(player)
                || !getInterface(IPvPManager.class).getConfig().getBoolean("on-logout.kill")
                || player.hasPermission("pvp.bypass.killonlogout")) {
            return;
        }

        getInterface(IPvPManager.class).removePlayerInCombat(player);
        player.setHealth(0);

        if (!getInterface(IPvPManager.class).getConfig().getBoolean("on-logout.broadcast")) {
            return;
        }

        for (final var onlinePlayer : Bukkit.getOnlinePlayers()) {
            getLanguage().message(onlinePlayer, Message.PvPManager.LOGOUT,
                    Pair.of(Placeholder.PLAYER, player.getName()));
        }
    }
}
