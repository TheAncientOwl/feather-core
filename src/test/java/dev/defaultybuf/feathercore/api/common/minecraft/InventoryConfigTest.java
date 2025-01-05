/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file InventoryConfigTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit InventoryConfig#0.1
 * @description Unit tests for InventoryConfig
 */

package dev.defaultybuf.feathercore.api.common.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import dev.defaultybuf.feathercore.api.configuration.IConfigSection;
import dev.defaultybuf.feathercore.api.configuration.IPropertyAccessor;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;

class InventoryConfigTest {
    @TestField ServerMock server = null;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testSerialize() {
        // Mock dependencies
        var mockConfig = mock(IPropertyAccessor.class);
        var mockInventory = mock(Inventory.class);
        var mockItemStack = mock(ItemStack.class);

        // Mock inventory size and item
        when(mockInventory.getSize()).thenReturn(9); // Inventory with 9 slots
        when(mockInventory.getItem(0)).thenReturn(mockItemStack);

        // Call the serialize method
        InventoryConfig.serialize(mockConfig, "test.path", mockInventory);

        // Verify the interactions
        verify(mockConfig).setInt("test.path.size", 9); // Size set
        verify(mockConfig).setItemStack("test.path.content.0", mockItemStack); // Item serialized
        verifyNoMoreInteractions(mockConfig);
    }

    @Test
    void testDeserialize() {
        // Mock dependencies
        var mockInventoryConfig = mock(IPropertyAccessor.class);
        var mockContent = mock(IConfigSection.class);
        var mockItemStack = new ItemStack(Material.DIAMOND);

        // Mock inventory size and display name
        when(mockInventoryConfig.getInt("size")).thenReturn(9);
        when(mockInventoryConfig.getString("display-name")).thenReturn("&aTest Inventory");

        // Mock configuration content
        when(mockInventoryConfig.getConfigurationSection("content")).thenReturn(mockContent);
        when(mockContent.getKeys(false)).thenReturn(Set.of("0"));
        when(mockContent.getItemStack("0")).thenReturn(mockItemStack); // Return mocked item

        // Call the method under test
        var inventory = InventoryConfig.deserialize(mockInventoryConfig);

        // Verify the inventory is not null and contains the mocked ItemStack
        assertNotNull(inventory, "Inventory should not be null");
        assertEquals(9, inventory.getSize());
        assertNotNull(inventory.getItem(0), "Item at position 0 should not be null");
        assertEquals(mockItemStack, inventory.getItem(0), "ItemStack should be set");
    }

    @Test
    void testDeserializeReturnsNullWhenConfigIsNull() {
        // Call the deserialize method with null
        var inventory = InventoryConfig.deserialize(null);

        // Verify that the result is null
        assertNull(inventory);
    }

    @Test
    void testDeserializeReturnsNullOnException() {
        // Mock dependencies
        var mockConfig = mock(IPropertyAccessor.class);

        // Mock an exception
        when(mockConfig.getConfigurationSection("content"))
                .thenThrow(new RuntimeException("Test exception"));

        // Call the deserialize method
        var inventory = InventoryConfig.deserialize(mockConfig);

        // Verify that the result is null
        assertNull(inventory);
    }

    @Test
    void dummyConstructor() {
        @SuppressWarnings("unused")
        var InventoryConfig = new InventoryConfig(); // InventoryConfig should contain only static
                                                     // methods
    }
}
