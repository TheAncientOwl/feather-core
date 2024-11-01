/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPCancelTpListener.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Cancel teleport on player move
 */

package mc.owls.valley.net.feathercore.modules.teleport.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.teleport.common.Message;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;

public class MoveCancelTpListener implements IFeatherListener {
    private Teleport teleport = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException {
        this.teleport = core.getTeleport();
        this.lang = core.getLanguageManager();
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
            if (this.teleport.cancelTeleport(player)) {
                this.lang.message(player, Message.TELEPORT_MOVED_WHILE_WAITING);
            }
        }
    }

}
