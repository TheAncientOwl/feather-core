package mc.owls.valley.net.feathercore.players.data.management.listeners;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.logging.FeatherLogger;
import mc.owls.valley.net.feathercore.players.data.management.PlayersDataManager;

public class PlayerJoinEventListener implements Listener {
    final PlayersDataManager dataManager;
    final FeatherLogger logger;

    public PlayerJoinEventListener(@NotNull final PlayersDataManager dataManager, final FeatherLogger logger) {
        this.dataManager = dataManager;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID playerUUID = player.getUniqueId();

        final PlayerModel playerModel = this.dataManager.getPlayerModel(playerUUID);
        if (playerModel == null) {
            this.logger.info(player.getName() + " joined for the first time!");
            this.dataManager.handleNewPlayer(player);
        } else {
            playerModel.lastLogin = new Date();
        }

        this.dataManager.markForSave(playerUUID);
    }
}
