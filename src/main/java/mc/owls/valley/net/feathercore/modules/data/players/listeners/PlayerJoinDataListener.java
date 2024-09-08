package mc.owls.valley.net.feathercore.modules.data.players.listeners;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import mc.owls.valley.net.feathercore.api.IFeatherListener;
import mc.owls.valley.net.feathercore.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.api.data.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.data.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class PlayerJoinDataListener implements IFeatherListener {
    private IPlayersDataManager dataManager = null;
    private IFeatherLoggger logger = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.dataManager = plugin.getPlayersDataManager();
        this.logger = plugin.getFeatherLogger();
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
