package mc.owls.valley.net.feathercore.modules.data.players.manager;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.logging.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigSection;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.manager.api.IPlayersDataManager;
import mc.owls.valley.net.feathercore.modules.data.players.manager.listeners.PlayerJoinEventListener;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;

public class PlayersDataManager extends FeatherModule implements IPlayersDataManager {
    private final Map<UUID, PlayerModel> playersDataCache = new HashMap<>();
    private Set<UUID> saveMarks = Collections.synchronizedSet(new HashSet<>());

    private IFeatherLoggger logger;
    private IConfigFile dataConfig;
    private PlayersDAO playersDAO;

    public PlayersDataManager(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) {
        this.logger = plugin.getFeatherLogger();
        this.dataConfig = plugin.getConfigurationManager().getDataConfiguration();
        this.playersDAO = plugin.getMongoManager().getPlayersDAO();

        registerEvents(plugin);

        setupAutoSave(plugin);

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
        savePlayersData();
    }

    @Override
    public void savePlayersData() {
        if (this.saveMarks.isEmpty()) {
            return;
        }

        final Set<UUID> set = this.saveMarks;
        this.saveMarks = Collections.synchronizedSet(new HashSet<>());

        for (UUID uuid : set) {
            final var playerModel = this.playersDataCache.getOrDefault(uuid, null);
            if (playerModel != null) {
                savePlayerModel(playerModel);
            }
        }
    }

    @Override
    public void handleNewPlayer(final Player player) {
        final PlayerModel playerModel = new PlayerModel();

        final IConfigSection newPlayerCfg = this.dataConfig.getConfigurationSection("players-data.new-player");

        playerModel.uuid = player.getUniqueId();
        playerModel.name = player.getName();
        playerModel.nickname = "";
        playerModel.registrationDate = new Date();
        playerModel.lastLogin = new Date();
        playerModel.balance = newPlayerCfg.getDouble("balance");

        this.playersDataCache.put(playerModel.uuid, playerModel);
        this.playersDAO.save(playerModel);
    }

    @Override
    @Deprecated
    public PlayerModel getPlayerModel(final String playerName) {
        PlayerModel playerModel = null;

        for (var playerData : this.playersDataCache.values()) {
            if (playerData.name.equals(playerName)) {
                playerModel = playerData;
            }
        }

        if (playerModel == null) {
            playerModel = this.playersDAO.getModelByName(playerName);
            if (playerModel != null) {
                this.playersDataCache.put(playerModel.uuid, playerModel);
            }
        }

        return playerModel;
    }

    @Override
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

    @Override
    public PlayerModel getPlayerModel(final Player player) {
        return getPlayerModel(player.getUniqueId());
    }

    @Override
    public PlayerModel getPlayerModel(final OfflinePlayer player) {
        return getPlayerModel(player.getUniqueId());
    }

    @Override
    public void savePlayerModel(final PlayerModel playerModel) {
        this.playersDAO.save(playerModel);
    }

    @Override
    @Deprecated
    public boolean markPlayerModelForSave(final String name) {
        final PlayerModel playerModel = getPlayerModel(name);
        if (playerModel != null) {
            markPlayerModelForSave(playerModel.uuid);
            return true;
        }
        return false;
    }

    public boolean markPlayerModelForSave(final UUID uuid) {
        if (!this.playersDataCache.containsKey(uuid)) {
            return false;
        }
        this.saveMarks.add(uuid);
        return true;
    }

    @Override
    public boolean markPlayerModelForSave(final Player player) {
        return markPlayerModelForSave(player.getUniqueId());
    }

    @Override
    public boolean markPlayerModelForSave(OfflinePlayer player) {
        return markPlayerModelForSave(player.getUniqueId());
    }

    private void registerEvents(final FeatherCore plugin) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinEventListener(this, this.logger), plugin);
    }

    private void setupAutoSave(final JavaPlugin plugin) {
        final IConfigSection autoSaveCfg = this.dataConfig.getConfigurationSection("players.auto-save");

        if (autoSaveCfg.getBoolean("enabled")) {
            final var minutes = autoSaveCfg.getInt("minutes");
            final var logging = autoSaveCfg.getBoolean("logging");

            final var period = minutes * 60 * 20L;

            if (minutes <= 0) {
                this.logger.error("players-daata.auto-save.minutes cannot be <= 0");
            } else if (logging) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    if (!this.saveMarks.isEmpty()) {
                        this.logger.info("Saving players data");
                    }
                    final int modelsCount = this.saveMarks.size();

                    savePlayersData();

                    if (modelsCount > 0) {
                        this.logger.info("Saved the data of " + modelsCount + " players");
                    }
                }, 0L, period);
            } else {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    savePlayersData();
                }, 0L, period);
            }
        }
    }
}
