/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageChangeListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit LanguageChangeListener#0.5
 * @description Unit tests for LanguageChangeListener
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import static dev.defaultybuf.feathercore.modules.common.Modules.injectAs;
import static dev.defaultybuf.feathercore.modules.common.Modules.withResources;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.modules.common.ListenerTestMocker;
import dev.defaultybuf.feathercore.modules.common.Modules;
import dev.defaultybuf.feathercore.modules.common.Resource;
import dev.defaultybuf.feathercore.modules.common.TempModule;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.language.events.LanguageChangeEvent;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

class LanguageChangeListenerTest extends ListenerTestMocker<LanguageChangeListener> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "economy:\n" +
                    "  banknote:\n" +
                    "    display-name: '&7Banknote'\n" +
                    "    lore:\n" +
                    "      - '&7Banknote value: &e{0}'";

    @Mock LanguageChangeEvent mockEvent;
    @Mock Player mockPlayer;
    @Mock PlayerInventory mockPlayerInventory;
    @Mock ItemStack mockItemStack;
    @Mock ItemMeta mockItemMeta;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    IFeatherEconomy mockFeatherEconomy;

    TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<LanguageChangeListener> getListenerClass() {
        return LanguageChangeListener.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        mockFeatherEconomy = Modules.ECONOMY.Mock();

        var config = mockFeatherEconomy.getConfig();
        Mockito.lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        return List.of(Pair.of(IFeatherEconomy.class, mockFeatherEconomy));
    }

    @Override
    protected List<AutoCloseable> injectActualModules() {
        actualLanguage = Modules.LANGUAGE.Actual(mockJavaPlugin, dependenciesMap,
                injectAs(ILanguage.class), withResources(
                        Resource.of("config.yml", LANGUAGE_CONFIG_CONTENT),
                        Resource.of("en.yml", EN_LANGUAGE_FILE_CONTENT)));

        return List.of(actualLanguage);
    }

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        var translation = actualLanguage.module().getTranslation("en");
        Mockito.lenient().when(mockEvent.getTranslation()).thenReturn(translation);
        Mockito.lenient().when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
        var inventoryContent = new ItemStack[] {null, mockItemStack, null, null};
        Mockito.lenient().when(mockPlayerInventory.getContents()).thenReturn(inventoryContent);
        Mockito.lenient().when(mockPlayerInventory.iterator())
                .thenReturn(Arrays.asList(inventoryContent).listIterator());
        Mockito.lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        Mockito.lenient().when(mockItemMeta.getPersistentDataContainer())
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
