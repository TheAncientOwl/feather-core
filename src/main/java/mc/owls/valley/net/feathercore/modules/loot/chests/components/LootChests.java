/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChests.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Module responsible for managing loot chests
 */

package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class LootChests extends FeatherModule {
    private IFeatherLogger logger = null;
    private IPlayersData playersData = null;
    private LootChestsDAO lootChestsDAO = null;
    private LootChestsModel lootChests = null;

    public LootChests(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.logger = core.getFeatherLogger();
        this.playersData = core.getPlayersData();
        this.lootChestsDAO = core.getMongoDB().getLootChestsDAO();
        this.lootChests = this.lootChestsDAO.getChests();
    }

    @Override
    protected void onModuleDisable() {
        this.logger.info("Saving chests data&7...");
        saveData();
        this.logger.info("Chests data saved&7.");
    }

    private void saveData() {
        this.lootChestsDAO.save(this.lootChests);
    }

    /**
     * Self explanatory
     * 
     * @param location
     * @param type
     */
    public void setChest(final String location, final String type) {
        this.lootChests.locationToType.put(location, type);
        saveData();
    }

    /**
     * Self explanatory
     * 
     * @param location
     */
    public void unsetChest(final String location) {
        this.lootChests.locationToType.remove(location);
        saveData();
    }

    /**
     * Self explanatory
     * 
     * @param location
     * @return null if there is no registered chest at given location
     */
    public String getChestType(final String location) {
        return this.lootChests.locationToType.get(location);
    }

    /**
     * Self explanatory
     * 
     * @param player
     * @param location
     * @return the time when @player opened chest at @location,
     *         null if the chest was not opened before
     */
    public Long getOpenChestTime(final Player player, final String location) {
        final PlayerModel playerModel = this.playersData.getPlayerModel(player);
        return playerModel.chestLocationToOpenTime.get(location);
    }

    /**
     * Self explanatory
     * 
     * @param player
     * @param location
     * @param now      usually System.now()
     */
    public void openChest(final Player player, final String chestType, final String location, final Long now) {
        final Inventory chest = this.config.getInventory("chests." + chestType);
        final PlayerModel playerModel = this.playersData.getPlayerModel(player);

        if (chest != null) {
            player.openInventory(chest);

            playerModel.chestLocationToOpenTime.put(location, now);
            this.playersData.markPlayerModelForSave(playerModel);
        } else {
            this.logger.warn("&8[&2Loot&aChests&8] &eUnknown chest type &6'" + chestType + "' &efound at location &6"
                    + location + "&e. Removing chest location from internal database.");
            unsetChest(location);
            playerModel.chestLocationToOpenTime.remove(location);
        }
    }

    /**
     * Saves given chest to config
     * 
     * @param type
     * @param displayName
     * @param cooldown
     * @param inventory
     */
    public void createChest(final String type, final String displayName, final long cooldown,
            final Inventory inventory) {
        final String configPath = "chests." + type;

        this.config.setSeconds(configPath + ".cooldown", cooldown);
        this.config.setString(configPath + ".display-name", displayName);
        this.config.setInventory(configPath, inventory);

        try {
            this.config.saveConfig();
        } catch (final Exception e) {
            this.logger.error(
                    "Could not save chest config to file loot-chests.yml. \nReason: " + StringUtils.exceptionToStr(e));
        }
    }

    /**
     * Removes given chest type from config
     * 
     * @param type
     */
    public void deleteChest(final String type) {
        this.config.remove("chests." + type);
    }

    /**
     * Self explanatory
     * 
     * @param type
     * @return list of chest locations of requested type
     */
    public List<String> getChestLocations(final String type) {
        final List<String> locations = new ArrayList<>();

        for (final var entry : this.lootChests.locationToType.entrySet()) {
            if (entry.getValue().equals(type)) {
                locations.add(entry.getKey());
            }
        }

        return locations;
    }

    /**
     * Self explanatory
     * 
     * @param type
     * @return true if @type exists in config, false otherwise
     */
    public boolean isChestType(final String type) {
        return this.config.getConfigurationSection("chests").getKeys(false).contains(type);
    }

}
