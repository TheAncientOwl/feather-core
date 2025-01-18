/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageChangeListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @test_unit LanguageChangeListener#0.5
 * @description Unit tests for LanguageChangeListener
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.events.LanguageChangeEvent;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.mockers.DependencyInjector.Module;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

class LanguageChangeListenerTest extends FeatherListenerTest<LanguageChangeListener> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";

    // @formatter:off
         static final String EN_LANGUAGE_FILE_CONTENT =
                        "economy:\n" +
                        "  banknote:\n" +
                        "    display-name: '&7Banknote'\n" +
                        "    lore:\n" +
                        "      - '&7Banknote value: &e{0}'";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock ItemMeta mockItemMeta;
    @Mock ItemStack mockItemStack;
    @Mock LanguageChangeEvent mockEvent;
    @Mock PlayerInventory mockPlayerInventory;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    @MockedModule(of = Module.Economy) IFeatherEconomy mockFeatherEconomy;

    @ActualModule(
            of = Module.Language,
            resources = {
                    @Resource(path = "config.yml",
                            content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml",
                            content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<LanguageChangeListener> getListenerClass() {
        return LanguageChangeListener.class;
    }

    @Override
    protected void setUp() {
        var config = mockFeatherEconomy.getConfig();
        lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        var translation = actualLanguage.module().getTranslation("en");
        lenient().when(mockEvent.getTranslation()).thenReturn(translation);
        lenient().when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
        var inventoryContent = new ItemStack[] {null, mockItemStack, null, null};
        lenient().when(mockPlayerInventory.getContents()).thenReturn(inventoryContent);
        lenient().when(mockPlayerInventory.iterator())
                .thenReturn(Arrays.asList(inventoryContent).listIterator());
        lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        lenient().when(mockItemMeta.getPersistentDataContainer())
                .thenReturn(mockPersistentDataContainer);
    }

    @Test
    void testOnLanguageChange_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onLanguageChange(mockEvent);

        verify(mockPlayerInventory, never()).getContents();
    }

    @Test
    void testOnLanguageChange_MetaNull() {
        when(mockItemStack.getItemMeta()).thenReturn(null);

        listenerInstance.onLanguageChange(mockEvent);

        verify(mockItemStack, never()).setItemMeta(any());
    }

    @Test
    void testOnLanguageChange_ValueKeyNotPresent() {
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(false);

        listenerInstance.onLanguageChange(mockEvent);

        verify(mockItemStack, never()).setItemMeta(any());
    }

    @Test
    void testOnLanguageChange_ValueKeyPresent() {
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(true);
        when(mockPersistentDataContainer.get(any(org.bukkit.NamespacedKey.class),
                eq(PersistentDataType.DOUBLE)))
                        .thenReturn(100.0);

        listenerInstance.onLanguageChange(mockEvent);

        verify(mockItemStack).setItemMeta(any());
    }
}
