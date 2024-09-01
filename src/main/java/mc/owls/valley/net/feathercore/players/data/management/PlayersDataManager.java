package mc.owls.valley.net.feathercore.players.data.management;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.FeatherCore;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.players.data.management.listeners.PlayerJoinEventListener;

public class PlayersDataManager {
    private Map<UUID, PlayerModel> playersDataCache = new HashMap<>();

    private final FeatherCore plugin;
    private final PlayersDAO playersDAO;

    public PlayersDataManager(final FeatherCore plugin) {
        this.plugin = plugin;
        this.playersDAO = plugin.getPlayersDAO();
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), plugin);
    }

    public void handleNewPlayer(final Player player) {
        final PlayerModel playerModel = new PlayerModel();

        ConfigurationSection newPlayerCfg = this.plugin.getConfig().getConfigurationSection("new-player");

        playerModel.uuid = player.getUniqueId();
        playerModel.username = player.getName();
        playerModel.nickname = "";
        playerModel.registrationDate = new Date();
        playerModel.lastLogin = new Date();
        playerModel.balance = newPlayerCfg.getDouble("balance");

        this.playersDataCache.put(playerModel.uuid, playerModel);
        this.playersDAO.save(playerModel);
    }

    public PlayerModel getPlayerModel(final UUID uuid) {
        PlayerModel playerModel = this.playersDataCache.getOrDefault(uuid, null);
        if (playerModel == null) {
            playerModel = playersDAO.get(uuid);
            if (playerModel != null) {
                this.playersDataCache.put(uuid, playerModel);
            }
        }
        return playerModel;
    }

    public void savePlayerModel(final PlayerModel playerModel) {
        this.playersDAO.save(playerModel);
    }
}
