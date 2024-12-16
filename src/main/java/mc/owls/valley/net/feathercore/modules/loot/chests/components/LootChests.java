/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChests.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Module responsible for managing loot chests
 */

package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.interfaces.IFeatherLoggerProvider;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.interfaces.IMongoDB;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.loot.chests.interfaces.ILootChests;

public class LootChests extends FeatherModule implements ILootChests {
    private LootChestsModel lootChests = null;

    public LootChests(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        this.lootChests = getInterface(IMongoDB.class).getLootChestsDAO().getChests();
    }

    @Override
    protected void onModuleDisable() {
        getInterface(IFeatherLoggerProvider.class).getFeatherLogger().info("Saving chests data&7...");
        saveData();
        getInterface(IFeatherLoggerProvider.class).getFeatherLogger().info("Chests data saved&7.");
    }

    private void saveData() {
        getInterface(IMongoDB.class).getLootChestsDAO().save(this.lootChests);
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
        final PlayerModel playerModel = getInterface(IPlayersData.class).getPlayerModel(player);
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
        final PlayerModel playerModel = getInterface(IPlayersData.class).getPlayerModel(player);

        if (chest != null) {
            player.openInventory(chest);

            playerModel.chestLocationToOpenTime.put(location, now);
            getInterface(IPlayersData.class).markPlayerModelForSave(playerModel);
        } else {
            getInterface(IFeatherLoggerProvider.class).getFeatherLogger()
                    .warn("&8[&2Loot&aChests&8] &eUnknown chest type &6'" + chestType + "' &efound at location &6"
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
            getInterface(IFeatherLoggerProvider.class).getFeatherLogger().error(
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
