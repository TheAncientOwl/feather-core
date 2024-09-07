package mc.owls.valley.net.feathercore.modules.economy.provider;

import org.bukkit.Server;
import org.bukkit.plugin.ServicePriority;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import net.milkbowl.vault.economy.Economy;

public class FeatherEconomyProvider extends FeatherModule {

    private FeatherEconomy economyProvider = null;

    public FeatherEconomyProvider(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws ModuleSetupException {
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new ModuleSetupException("Vault dependency is not installed");
        }

        this.economyProvider = new FeatherEconomy(plugin.getPlayersDataManager());
        server.getServicesManager().register(Economy.class, this.economyProvider, plugin, ServicePriority.High);

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
