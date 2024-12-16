/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherEconomyProvider.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Module responsible for managing vault/server Economy
 */

package mc.owls.valley.net.feathercore.modules.economy.components;

import java.util.function.Supplier;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IEconomyProvider;
import net.milkbowl.vault.economy.Economy;

public class FeatherEconomyProvider extends FeatherModule implements IEconomyProvider {
    private Economy economy = null;

    public FeatherEconomyProvider(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        provideEconomy(core);
        setupVault(core);
    }

    private void provideEconomy(final IFeatherCoreProvider core) throws FeatherSetupException {
        final JavaPlugin plugin = core.getPlugin();
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }

        final FeatherEconomy featherEconomy = new FeatherEconomy(core.getPlayersData(), getConfig());
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
