package mc.owls.valley.net.feathercore.modules.economy.manager;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;

public class EconomyManager extends FeatherModule {

    public EconomyManager(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(FeatherCore plugin) throws ModuleSetupException {
        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
