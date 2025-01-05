/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherEconomyProviderTest.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @test_unit FeatherEconomyProvider#0.8
 * @description Unit tests for FeatherEconomyProvider
 */

package dev.defaultybuf.feathercore.modules.economy.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherModuleTest;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import net.milkbowl.vault.economy.Economy;

class FeatherEconomyProviderTest extends FeatherModuleTest<FeatherEconomyProvider> {
    @Mock IPlayersData mockPlayersData;
    @Mock PluginManager mockPluginManager;
    @Mock ServicesManager mockServicesManager;
    @Mock RegisteredServiceProvider<Economy> mockRegisteredServiceProvider;

    @Override
    protected Class<FeatherEconomyProvider> getModuleClass() {
        return FeatherEconomyProvider.class;
    }

    @Override
    protected String getModuleName() {
        return DependencyInjector.Economy.name();
    }

    @Override
    protected void setUp() {
        lenient().when(mockServer.getPluginManager()).thenReturn(mockPluginManager);
        lenient().when(mockServer.getServicesManager()).thenReturn(mockServicesManager);
    }

    @Test
    void testOnModuleEnable_VaultNotInstalled() {
        when(mockPluginManager.getPlugin("Vault")).thenReturn(null);

        var exception = assertThrows(FeatherSetupException.class, () -> {
            moduleInstance.onModuleEnable();
        });

        assertEquals("Vault dependency is not installed", exception.getMessage());
    }

    @Test
    void testOnModuleEnable_NotNullRSP() throws FeatherSetupException {
        when(mockPluginManager.getPlugin("Vault")).thenReturn(mock(JavaPlugin.class));
        when(mockServicesManager.getRegistration(Economy.class))
                .thenReturn(mockRegisteredServiceProvider);
        when(mockRegisteredServiceProvider.getProvider()).thenReturn(mock(Economy.class));

        assertDoesNotThrow(() -> moduleInstance.onModuleEnable());

        verify(mockServicesManager).register(eq(Economy.class), any(), eq(mockJavaPlugin),
                eq(ServicePriority.High));
        assertNotNull(moduleInstance.getEconomy());
    }

    @Test
    void testOnModuleEnable_NULL_RSP() throws FeatherSetupException {
        when(mockPluginManager.getPlugin("Vault")).thenReturn(mock(JavaPlugin.class));
        when(mockServicesManager.getRegistration(Economy.class)).thenReturn(null);

        var exception = assertThrows(FeatherSetupException.class, () -> {
            moduleInstance.onModuleEnable();
        });

        assertEquals("Could not find feather economy provider!", exception.getMessage());

        verify(mockServicesManager).register(eq(Economy.class), any(), eq(mockJavaPlugin),
                eq(ServicePriority.High));
        assertNull(moduleInstance.getEconomy());
    }

    @Test
    void testOnModuleDisable() {
        assertDoesNotThrow(() -> moduleInstance.onModuleDisable());
    }
}
