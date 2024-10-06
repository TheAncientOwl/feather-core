/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayersData.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Module responsible for managing plugin players data
 */

package mc.owls.valley.net.feathercore.modules.data.players.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class PlayersData extends FeatherModule implements IPlayersData {
    private final Map<UUID, PlayerModel> playersDataCache = new HashMap<>();
    private Set<UUID> saveMarks = Collections.synchronizedSet(new HashSet<>());

    private IFeatherLogger logger = null;
    private PlayersDAO playersDAO = null;

    public PlayersData(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) {
        this.logger = core.getFeatherLogger();
        this.playersDAO = core.getMongoDB().getPlayersDAO();

        setupAutoSave(core.getPlugin());
    }

    @Override
    protected void onModuleDisable() {
        this.logger.info("Saving players data&7...");
        final var playersCount = this.saveMarks.size();
        savePlayersData();
        this.logger.info("Saved the data of " + playersCount + " players&7.");
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
        final PlayerModel playerModel = new PlayerModel(player, this.config.getDouble("starting-balance"),
                this.config.getString("default-language"));

        this.playersDataCache.put(playerModel.uuid, playerModel);
        this.playersDAO.save(playerModel);
    }

    @Override
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
    public boolean markPlayerModelForSave(final OfflinePlayer player) {
        return markPlayerModelForSave(player.getUniqueId());
    }

    @Override
    public boolean markPlayerModelForSave(final PlayerModel playerModel) {
        return markPlayerModelForSave(playerModel.uuid);
    }

    private void setupAutoSave(final JavaPlugin plugin) {
        final IConfigSection autoSaveCfg = this.config.getConfigurationSection("auto-save");

        if (autoSaveCfg.getBoolean("enabled")) {
            final var ticks = autoSaveCfg.getTicks("time");
            final var logging = autoSaveCfg.getBoolean("logging");

            if (ticks <= 0) {
                this.logger.error("players-data.auto-save.time cannot be <= 0");
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
                }, 0L, ticks);
            } else {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    savePlayersData();
                }, 0L, ticks);
            }
        }
    }

}
