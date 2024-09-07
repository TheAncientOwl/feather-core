package mc.owls.valley.net.feathercore.modules.economy.vault;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import mc.owls.valley.net.feathercore.api.IEconomyProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import net.milkbowl.vault.economy.Economy;

public class VaultModule extends FeatherModule implements IEconomyProvider {
    private Economy economy = null;

    public VaultModule(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }

        final RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new FeatherSetupException("Could not find feather economy provider!");
        }

        this.economy = rsp.getProvider();

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

    @Override
    public Economy getEconomy() {
        return this.economy;
    }

}
