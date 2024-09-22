package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.database.mongo.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.api.database.mongo.models.LootChestsModel;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;

public class LootChests extends FeatherModule implements ILootChestsModule {
    private IFeatherLogger logger = null;
    private IPlayersDataManager playersData = null;
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
        saveData();
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
    public void openChest(final Player player, final Inventory chestInventory, final String location, final Long now) {
        final PlayerModel playerModel = this.playersData.getPlayerModel(player);
        playerModel.chestLocationToOpenTime.put(location, now);
        this.playersData.markPlayerModelForSave(playerModel);
        player.openInventory(chestInventory);
    }

    @Override
    public Inventory getChestInventory(final String type) {
        return this.config.getInventory("chests." + type);
    }

    @Override
    public void createChest(final String type, final String displayName, final long cooldown,
            final Inventory inventory) {
        this.config.setString("chests." + type + ".display-name", displayName);
        final IPropertyAccessor chestConfig = this.config.getConfigurationSection("chests." + type);

        chestConfig.setLong("cooldown", cooldown);
        chestConfig.setInt("size", inventory.getSize());
        chestConfig.setInventory("inventory", inventory);

        try {
            this.config.saveConfig();
        } catch (final Exception e) {
            this.logger.error(
                    "Could not save chest config to file loot-chests.yml. \nReason: " + StringUtils.exceptionToStr(e));
        }
    }

    @Override
    public void deleteChest(final String type) {

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
