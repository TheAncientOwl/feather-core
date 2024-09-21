package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;

public class LootChests extends FeatherModule implements ILootChestsModule {
    private IPlayersDataManager playersData = null;
    private ITranslationAccessor lang = null;
    private IConfigFile config = null;

    public LootChests(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.playersData = core.getPlayersDataManager();
        this.lang = core.getTranslationManager();
        this.config = core.getConfigurationManager().getLootChestsConfigFile();
    }

    @Override
    protected void onModuleDisable() {
    }

    @Override
    public void addChest(Location location, String type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addChest'");
    }

    @Override
    public Inventory getChestInventory(Location location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChestInventory'");
    }

}
