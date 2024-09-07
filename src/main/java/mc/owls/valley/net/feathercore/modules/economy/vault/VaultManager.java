package mc.owls.valley.net.feathercore.modules.economy.vault;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import net.milkbowl.vault.economy.Economy;

public class VaultManager extends FeatherModule {
    private Economy economy = null;

    public VaultManager(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws ModuleSetupException {
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new ModuleSetupException("Vault dependency is not installed");
        }

        final RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new ModuleSetupException("Could not find feather economy provider!");
        }

        this.economy = rsp.getProvider();

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

    public Economy getEconomy() {
        return this.economy;
    }

}
