/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportListener.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Block combat teleport
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class TeleportListener extends FeatherListener {
    public TeleportListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (event.isCancelled() || !getInterface(IPvPManager.class).isPlayerInCombat(player)
                || player.hasPermission("pvp.bypass.teleport")) {
            return;
        }

        if ((event.getCause() == TeleportCause.CHORUS_FRUIT
                && !getInterface(IPvPManager.class).getConfig().getBoolean("block-tp.chorus-fruit"))
                ||
                (event.getCause() == TeleportCause.ENDER_PEARL
                        && !getInterface(IPvPManager.class).getConfig()
                                .getBoolean("block-tp.ender-pearl"))) {
            event.setCancelled(true);
            getLanguage().message(player, Message.PvPManager.TELEPORT);
        }
    }
}
