package mc.owls.valley.net.feathercore.players.data.management;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.FeatherCore;
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerModel;
import mc.owls.valley.net.feathercore.players.data.management.listeners.PlayerJoinEventListener;

public class PlayersDataManager {
    private final Map<UUID, PlayerModel> playersDataCache = new HashMap<>();
    private Set<UUID> saveSet = Collections.synchronizedSet(new HashSet<>());

    private final FeatherCore plugin;
    private final PlayersDAO playersDAO;

    public PlayersDataManager(final FeatherCore plugin) {
        this.plugin = plugin;
        this.playersDAO = plugin.getPlayersDAO();
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), plugin);

        final ConfigurationSection autoSaveCfg = this.plugin.getConfig()
                .getConfigurationSection("players-data.auto-save");
        if (autoSaveCfg.getBoolean("enabled")) {
            final var minutes = autoSaveCfg.getInt("minutes");
            final var logging = autoSaveCfg.getBoolean("logging");

            final var period = minutes * 60 * 20L;

            if (minutes <= 0) {
                FeatherCore.GetFeatherLogger().error("players-daata.auto-save.minutes cannot be <= 0");
            } else if (logging) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                    if (!this.saveSet.isEmpty()) {
                        FeatherCore.GetFeatherLogger().info("Saving players data");
                    }
                    final int modelsCount = this.saveSet.size();

                    this.savePlayersData();

                    if (modelsCount > 0) {
                        FeatherCore.GetFeatherLogger().info("Saved the data of " + modelsCount + " players");
                    }
                }, 0L, period);
            } else {
                Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                    this.savePlayersData();
                }, 0L, period);
            }
        }
    }

    public void savePlayersData() {
        if (this.saveSet.isEmpty()) {
            return;
        }

        final Set<UUID> set = this.saveSet;
        this.saveSet = Collections.synchronizedSet(new HashSet<>());

        for (UUID uuid : set) {
            final var playerModel = this.playersDataCache.getOrDefault(uuid, null);
            if (playerModel != null) {
                this.savePlayerModel(playerModel);
            }
        }
    }

    public void handleNewPlayer(final Player player) {
        final PlayerModel playerModel = new PlayerModel();

        final ConfigurationSection newPlayerCfg = this.plugin.getConfig()
                .getConfigurationSection("players-data.new-player");

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

    public boolean markForSaving(final UUID uuid) {
        if (!this.playersDataCache.containsKey(uuid)) {
            return false;
        }
        this.saveSet.add(uuid);
        return true;
    }
}
