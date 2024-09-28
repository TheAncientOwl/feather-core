package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigFile;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class LootChests extends FeatherModule implements ILootChestsModule {
    private IFeatherLogger logger = null;
    private IPlayersData playersData = null;
    private IConfigFile config = null;
    private LootChestsDAO lootChestsDAO = null;
    private LootChestsModel lootChests = null;

    public LootChests(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.logger = core.getFeatherLogger();
        this.playersData = core.getPlayersDataManager();
        this.config = core.getConfigurationManager().getLootChestsConfigFile();
        this.lootChestsDAO = core.getMongoDAO().getLootChestsDAO();
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

    @Override
    public void setChest(final String location, final String type) {
        this.lootChests.locationToType.put(location, type);
        saveData();
    }

    @Override
    public void unsetChest(final String location) {
        this.lootChests.locationToType.remove(location);
        saveData();
    }

    @Override
    public String getChestType(final String location) {
        return this.lootChests.locationToType.get(location);
    }

    @Override
    public Long getOpenChestTime(final Player player, final String location) {
        final PlayerModel playerModel = this.playersData.getPlayerModel(player);
        return playerModel.chestLocationToOpenTime.get(location);
    }

    @Override
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

    @Override
    public void createChest(final String type, final String displayName, final long cooldown,
            final Inventory inventory) {
        final String configPath = "chests." + type;

        this.config.setLong(configPath + ".cooldown", cooldown);
        this.config.setString(configPath + ".display-name", displayName);
        this.config.setInventory(configPath, inventory);

        try {
            this.config.saveConfig();
        } catch (final Exception e) {
            this.logger.error(
                    "Could not save chest config to file loot-chests.yml. \nReason: " + StringUtils.exceptionToStr(e));
        }
    }

    @Override
    public void deleteChest(final String type) {
        this.config.remove("chests." + type);
    }

    @Override
    public List<String> getChestLocations(final String type) {
        final List<String> locations = new ArrayList<>();

        for (final var entry : this.lootChests.locationToType.entrySet()) {
            if (entry.getValue().equals(type)) {
                locations.add(entry.getKey());
            }
        }

        return locations;
    }

    @Override
    public boolean isChestType(final String type) {
        return this.config.getConfigurationSection("chests").getKeys(false).contains(type);
    }

}
