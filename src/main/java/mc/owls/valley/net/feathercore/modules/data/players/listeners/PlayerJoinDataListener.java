/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerJoinDataListener.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Setup new players data; Update on-login data
 */

package mc.owls.valley.net.feathercore.modules.data.players.listeners;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import mc.owls.valley.net.feathercore.api.core.FeatherListener;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LocationModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class PlayerJoinDataListener extends FeatherListener {
    public PlayerJoinDataListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        final PlayerModel playerModel = getInterface(IPlayersData.class).getPlayerModel(playerUUID);
        if (playerModel == null) {
            getInterface(IFeatherLogger.class).info(player.getName() + " joined for the first time!");
            getInterface(IPlayersData.class).handleNewPlayer(player);
        } else {
            playerModel.lastLogin = new Date();
            playerModel.lastKnownLocation = new LocationModel(player.getLocation());
            playerModel.compatibilityUpdate(player);
        }

        getInterface(IPlayersData.class).markPlayerModelForSave(playerUUID);
    }
}
