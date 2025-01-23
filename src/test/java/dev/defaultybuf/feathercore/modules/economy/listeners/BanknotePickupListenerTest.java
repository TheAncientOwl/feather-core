/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickupListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.15
 * @test_unit BanknotePickupListener#0.5
 * @description Unit tests for BanknotePickupListener
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feathercore.common.minecraft.NamespacedKey;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class BanknotePickupListenerTest extends FeatherListenerTest<BanknotePickupListener> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";

    // @formatter:off
     static final String EN_LANGUAGE_FILE_CONTENT =
        "economy:\n" +
        "  banknote:\n" +
        "    display-name: '&7Banknote'\n" +
        "    lore:\n" +
        "      - '&7Banknote value: &e{0}'";
    // @formatter:on

    @Mock Item mockItem;
    @Mock Player mockPlayer;
    @Mock ItemMeta mockItemMeta;
    @Mock ItemStack mockItemStack;
    @Mock NamespacedKey mockNamespacedKey;
    @Mock EntityPickupItemEvent mockEvent;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    @MockedModule IPlayersData mockPlayersData;
    @MockedModule IFeatherEconomy mockFeatherEconomy;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<BanknotePickupListener> getListenerClass() {
        return BanknotePickupListener.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockEvent.getEntity()).thenReturn(mockPlayer);
        lenient().when(mockEvent.getEntityType()).thenReturn(EntityType.PLAYER);
        lenient().when(mockEvent.getItem()).thenReturn(mockItem);
        lenient().when(mockItem.getItemStack()).thenReturn(mockItemStack);
        lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);

    }

    @Test
    void testOnItemPickup_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).getItemMeta();
    }

    @Test
    void testOnItemPickup_NotPlayer() {
        when(mockEvent.getEntityType()).thenReturn(EntityType.ZOMBIE);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).getItemMeta();
    }

    @Test
    void testOnItemPickup_MetaNull() {
        when(mockItemStack.getItemMeta()).thenReturn(null);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).setItemMeta(any());
    }

    @Test
    void testOnItemPickup_NamespacedKeyNotPresent() {
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockPersistentDataContainer);
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(false);

        var config = mockFeatherEconomy.getConfig();
        when(config.getString("banknote.key")).thenReturn("banknote_key");

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack).getItemMeta();
    }

    @Test
    void testOnItemPickup_Success() {
        var config = mockFeatherEconomy.getConfig();
        when(config.getString("banknote.key")).thenReturn("banknote_key");

        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockPersistentDataContainer);
        when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                .thenReturn(true);

        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack).getItemMeta();
        verify(mockItemStack).setItemMeta(any());
    }
}
