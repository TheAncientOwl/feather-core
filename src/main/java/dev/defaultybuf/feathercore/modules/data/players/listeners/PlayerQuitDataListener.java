/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerQuitDataListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Update on-quit data
 */

package dev.defaultybuf.feathercore.modules.data.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.defaultybuf.feather.toolkit.api.FeatherListener;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LocationModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;

public class PlayerQuitDataListener extends FeatherListener {
    public PlayerQuitDataListener(final InitData data) {
        super(data);
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final var player = event.getPlayer();

        final var playerModel = getInterface(IPlayersData.class).getPlayerModel(player);
        playerModel.lastKnownLocation = new LocationModel(player.getLocation());

        getInterface(IPlayersData.class).markPlayerModelForSave(playerModel);
    }
}
