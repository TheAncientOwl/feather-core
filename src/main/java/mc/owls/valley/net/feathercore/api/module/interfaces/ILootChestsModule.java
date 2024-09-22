package mc.owls.valley.net.feathercore.api.module.interfaces;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ILootChestsModule {
    /**
     * Self explanatory
     * 
     * @param location
     * @param type
     */
    public void setChest(final String location, final String type);

    /**
     * Self explanatory
     * 
     * @param location
     */
    public void unsetChest(final String location);

    /**
     * Self explanatory
     * 
     * @param location
     * @return null if there is no registered chest at given location
     */
    public String getChestType(final String location);

    /**
     * Self explanatory
     * 
     * @param player
     * @param location
     * @return the time when @player opened chest at @location,
     *         null if the chest was not opened before
     */
    public Long getOpenChestTime(final Player player, final String location);

    /**
     * Self explanatory
     * 
     * @param player
     * @param location
     * @param now      usually System.now()
     */
    public void openChest(final Player player, final Inventory chestInventory, final String location, final Long now);

    /**
     * Self explanatory
     * 
     * @param type
     * @return inventory of the @type chest
     */
    public Inventory getChestInventory(final String type);

    /**
     * Saves given chest to config
     * 
     * @param type
     * @param displayName
     * @param cooldown
     * @param inventory
     */
    public void createChest(final String type, final String displayName, final long cooldown,
            final Inventory inventory);

    /**
     * Removes given chest type from config
     * 
     * @param type
     */
    public void deleteChest(final String type);

    /**
     * Self explanatory
     * 
     * @param type
     * @return list of chest locations of requested type
     */
    public List<String> getChestLocations(final String type);

    /**
     * Self explanatory
     * 
     * @param type
     * @return true if @type exists in config, false otherwise
     */
    public boolean isChestType(final String type);
}
