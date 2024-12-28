/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherEconomyProvider.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @description Module responsible for managing vault/server Economy
 */

package mc.owls.valley.net.feathercore.modules.economy.components;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomy;
import net.milkbowl.vault.economy.Economy;

public class FeatherEconomyProvider extends FeatherModule implements IFeatherEconomy {
    public FeatherEconomyProvider(final InitData data) {
        super(data);
    }

    private Economy economy = null;

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        assertVault();
        provideEconomy();
        setupVault();
    }

    private void assertVault() throws FeatherSetupException {
        final JavaPlugin plugin = getPlugin();
        final Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new FeatherSetupException("Vault dependency is not installed");
        }
    }

    private void provideEconomy() throws FeatherSetupException {
        final JavaPlugin plugin = getPlugin();
        final Server server = plugin.getServer();

        final FeatherEconomy featherEconomy =
                new FeatherEconomy(getInterface(IPlayersData.class), getConfig());
        server.getServicesManager().register(Economy.class, featherEconomy, plugin,
                ServicePriority.High);
    }

    private void setupVault() throws FeatherSetupException {
        final JavaPlugin plugin = getPlugin();
        final Server server = plugin.getServer();

        final RegisteredServiceProvider<Economy> rsp =
                server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new FeatherSetupException("Could not find feather economy provider!");
        }

        this.economy = rsp.getProvider();
    }

    @Override
    protected void onModuleDisable() {}

    @Override
    public Economy getEconomy() {
        return this.economy;
    }

    @Override
    public IConfigFile getConfig() {
        return this.config;
    }
}
