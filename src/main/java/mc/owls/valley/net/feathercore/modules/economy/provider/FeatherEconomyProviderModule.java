package mc.owls.valley.net.feathercore.modules.economy.provider;

import org.bukkit.Server;
import org.bukkit.plugin.ServicePriority;

import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import net.milkbowl.vault.economy.Economy;

public class FeatherEconomyProviderModule extends FeatherModule {
    private FeatherEconomy economyProvider = null;

    public FeatherEconomyProviderModule(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }

        this.economyProvider = new FeatherEconomy(plugin.getPlayersDataManager(),
                plugin.getConfigurationManager().getEconomyConfigFile());
        server.getServicesManager().register(Economy.class, this.economyProvider, plugin, ServicePriority.High);

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
    }

}
