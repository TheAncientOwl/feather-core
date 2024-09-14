package mc.owls.valley.net.feathercore.modules.economy.components;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.IEconomyProvider;
import net.milkbowl.vault.economy.Economy;

public class FeatherEconomyProvider extends FeatherModule implements IEconomyProvider {
    private Economy economy = null;

    public FeatherEconomyProvider(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        provideEconomy(core);
        setupVault(core);

        return ModuleEnableStatus.SUCCESS;
    }

    private void provideEconomy(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }

        final FeatherEconomy featherEconomy = new FeatherEconomy(core.getPlayersDataManager(),
                core.getConfigurationManager().getEconomyConfigFile());
        server.getServicesManager().register(Economy.class, featherEconomy, plugin, ServicePriority.High);
    }

    private void setupVault(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }

        final RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new FeatherSetupException("Could not find feather economy provider!");
        }

        this.economy = rsp.getProvider();
    }

    @Override
    protected void onModuleDisable() {
    }

    @Override
    public Economy getEconomy() {
        return this.economy;
    }

}
