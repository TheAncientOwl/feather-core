/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPCancelTpListener.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Cancel teleport on player move
 */

package mc.owls.valley.net.feathercore.modules.teleport.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.core.FeatherListener;
import mc.owls.valley.net.feathercore.modules.teleport.interfaces.ITeleport;

public class MoveCancelTpListener extends FeatherListener {
    public MoveCancelTpListener(final InitData data) {
        super(data);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final var from = event.getFrom();
        final var to = event.getTo();

        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != from.getZ()) {
            final var player = event.getPlayer();
            if (getInterface(ITeleport.class).cancelTeleport(player)) {
                getLanguage().message(player, Message.Teleport.MOVED_WHILE_WAITING);
            }
        }
    }
}
