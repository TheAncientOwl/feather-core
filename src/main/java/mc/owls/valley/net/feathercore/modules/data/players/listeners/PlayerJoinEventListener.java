package mc.owls.valley.net.feathercore.modules.data.players.listeners;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.api.data.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.PlayersDataModule;

public class PlayerJoinEventListener implements Listener {
    final PlayersDataModule dataManager;
    final IFeatherLoggger logger;

    public PlayerJoinEventListener(@NotNull final PlayersDataModule dataManager, final IFeatherLoggger logger) {
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

        this.dataManager.markPlayerModelForSave(playerUUID);
    }
}
