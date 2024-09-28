package mc.owls.valley.net.feathercore.modules.data.players.listeners;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class PlayerJoinDataListener implements IFeatherListener {
    private IPlayersData dataManager = null;
    private IFeatherLogger logger = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.dataManager = core.getPlayersDataManager();
        this.logger = core.getFeatherLogger();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
