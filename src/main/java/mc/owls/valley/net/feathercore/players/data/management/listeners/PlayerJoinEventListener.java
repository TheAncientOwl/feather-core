package mc.owls.valley.net.feathercore.players.data.management.listeners;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.FeatherCore;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.players.data.management.PlayersDataManager;

public class PlayerJoinEventListener implements Listener {
    final PlayersDataManager dataManager;

    public PlayerJoinEventListener(@NotNull final PlayersDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        final PlayerModel playerModel = this.dataManager.getPlayerModel(player.getUniqueId());
        if (playerModel == null) {
            FeatherCore.GetFeatherLogger().info(player.getName() + " joined for the first time!");
            this.dataManager.handleNewPlayer(player);
        } else {
            playerModel.lastLogin = new Date();
        }
    }
}
