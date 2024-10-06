/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerQuitDataListener.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Update on-quit data
 */

package mc.owls.valley.net.feathercore.modules.data.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LocationModel;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;

public class PlayerQuitDataListener implements IFeatherListener {
    private PlayersData playersData = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException {
        this.playersData = core.getPlayersData();
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final var player = event.getPlayer();

        final var playerModel = this.playersData.getPlayerModel(player);
        playerModel.lastKnownLocation = new LocationModel(player.getLocation());

        this.playersData.markPlayerModelForSave(playerModel);
    }

}
