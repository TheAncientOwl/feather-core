/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file NamespacedKeyTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit NamespacedKey#0.2
 * @description Unit tests for NamespacedKey
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NamespacedKeyTest {

    private JavaPlugin mockPlugin;
    private ItemMeta mockItemMeta;
    private PersistentDataContainer mockDataContainer;
    private PersistentDataType<String, Integer> mockDataType;
    private NamespacedKey namespacedKey;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        mockPlugin = mock(JavaPlugin.class);
        mockItemMeta = mock(ItemMeta.class);
        mockDataContainer = mock(PersistentDataContainer.class);
        mockDataType = mock(PersistentDataType.class);

        when(mockPlugin.getName()).thenReturn("TestPlugin");
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockDataContainer);

        namespacedKey = new NamespacedKey(mockPlugin, mockItemMeta, "testKey");
    }

    @Test
    void testIsPresent_WhenContainerIsNull() {
        // Simulate container being null
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(null);

        assertFalse(namespacedKey.isPresent()); // Expecting false because container is null
    }

    @Test
    void testIsPresent_WhenKeyIsPresent() {
        // Simulate the key being present
        when(mockDataContainer.has(any())).thenReturn(true);

        assertTrue(namespacedKey.isPresent()); // Expecting true because the key is present
    }

    @Test
    void testIsPresent_WhenKeyIsNotPresent() {
        // Simulate the key being absent
        when(mockDataContainer.has(any())).thenReturn(false);

        assertFalse(namespacedKey.isPresent()); // Expecting false because the key is not present
    }

    @Test
    void testIsPresent_WhenContainerIsNotNullAndKeyIsAbsent() {
        // Simulate container being non-null and key being absent
        when(mockDataContainer.has(any())).thenReturn(false);

        assertFalse(namespacedKey.isPresent()); // Expecting false because key is absent
    }

    @Test
    void testIsPresent_WhenContainerIsNotNullAndKeyIsPresent() {
        // Simulate container being non-null and key being present
        when(mockDataContainer.has(any())).thenReturn(true);

        assertTrue(namespacedKey.isPresent()); // Expecting true because key is present
    }

    @Test
    void testGet_ReturnsValue() {
        // Simulate the key having a value
        when(mockDataContainer.get(any(), eq(mockDataType))).thenReturn(100);

        Integer value = namespacedKey.get(mockDataType);

        assertEquals(100, value); // Verify the value returned is as expected
    }

    @Test
    void testSet_SetsValue() {
        // Set up mock behavior to verify setting a value
        namespacedKey.set(mockDataType, 200);

        // Verify that set() method was called on the PersistentDataContainer
        verify(mockDataContainer).set(any(), eq(mockDataType), eq(200));
    }
}