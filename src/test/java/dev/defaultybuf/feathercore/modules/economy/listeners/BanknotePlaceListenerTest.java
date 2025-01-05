/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePlaceListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit BanknotePlaceListener#0.6
 * @description Unit tests for BanknotePlaceListener
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherListenerTest;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

class BanknotePlaceListenerTest extends FeatherListenerTest<BanknotePlaceListener> {
    @Mock BlockPlaceEvent mockEvent;
    @Mock ItemStack mockItemStack;
    @Mock ItemMeta mockItemMeta;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    @MockedModule(of = Module.Economy) IFeatherEconomy mockFeatherEconomy;

    @Override
    protected Class<BanknotePlaceListener> getListenerClass() {
        return BanknotePlaceListener.class;
    }

    @Override
    protected void setUp() {
        var config = mockFeatherEconomy.getConfig();
        lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        lenient().when(mockEvent.getItemInHand()).thenReturn(mockItemStack);
        lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        lenient().when(mockItemMeta.getPersistentDataContainer())
                .thenReturn(mockPersistentDataContainer);
    }

    @Test
    void testOnBlockPlace_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onBlockPlace(mockEvent);

        verify(mockItemStack, never()).getItemMeta();
    }

    @Test
    void testOnBlockPlace_MetaNull() {
        when(mockItemStack.getItemMeta()).thenReturn(null);

        listenerInstance.onBlockPlace(mockEvent);

        verify(mockItemStack, never()).setItemMeta(any());
    }

    @Test
    void testOnBlockPlace_ValueKeyNotPresent() {
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockPersistentDataContainer);
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(false);

        listenerInstance.onBlockPlace(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnBlockPlace_ValueKeyPresent() {
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockPersistentDataContainer);
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(true);

        listenerInstance.onBlockPlace(mockEvent);

        verify(mockEvent).setCancelled(true);
    }
}
