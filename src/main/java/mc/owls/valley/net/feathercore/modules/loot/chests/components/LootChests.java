package mc.owls.valley.net.feathercore.modules.loot.chests.components;

import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;

public class LootChests extends FeatherModule {

    public LootChests(String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
    }

    @Override
    protected void onModuleDisable() {
    }

}
