/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickupListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit BanknotePickupListener#0.5
 * @description Unit tests for BanknotePickupListener
 */

package mc.owls.valley.net.feathercore.modules.economy.listeners;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.modules.common.ListenerTestMocker;
import mc.owls.valley.net.feathercore.modules.common.Modules;
import mc.owls.valley.net.feathercore.modules.common.TestUtils;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomy;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

class BanknotePickupListenerTest extends ListenerTestMocker<BanknotePickupListener> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";

    private static final String EN_LANGUAGE_FILE_CONTENT =
            "economy:\n" +
                    "  banknote:\n" +
                    "    display-name: '&7Banknote'\n" +
                    "    lore:\n" +
                    "      - '&7Banknote value: &e{0}'";

    @Mock EntityPickupItemEvent mockEvent;
    @Mock Player mockPlayer;
    @Mock Item mockItem;
    @Mock ItemStack mockItemStack;
    @Mock ItemMeta mockItemMeta;
    @Mock NamespacedKey mockNamespacedKey;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    IFeatherEconomy mockFeatherEconomy;
    IPlayersData mockPlayersData;

    @Override
    protected Class<BanknotePickupListener> getListenerClass() {
        return BanknotePickupListener.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        mockFeatherEconomy = Modules.ECONOMY.Mock();
        mockPlayersData = Modules.PLAYERS_DATA.Mock();
        return List.of(
                Pair.of(IFeatherEconomy.class, mockFeatherEconomy),
                Pair.of(IPlayersData.class, mockPlayersData));
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.lenient().when(mockEvent.getEntity()).thenReturn(mockPlayer);
        Mockito.lenient().when(mockEvent.getEntityType()).thenReturn(EntityType.PLAYER);
        Mockito.lenient().when(mockEvent.getItem()).thenReturn(mockItem);
        Mockito.lenient().when(mockItem.getItemStack()).thenReturn(mockItemStack);
        Mockito.lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
    }

    @Test
    public void testOnItemPickup_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).getItemMeta();
    }

    @Test
    public void testOnItemPickup_NotPlayer() {
        when(mockEvent.getEntityType()).thenReturn(EntityType.ZOMBIE);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).getItemMeta();
    }

    @Test
    public void testOnItemPickup_MetaNull() {
        when(mockItemStack.getItemMeta()).thenReturn(null);

        listenerInstance.onItemPickup(mockEvent);

        verify(mockItemStack, never()).setItemMeta(any());
    }

    @Test
    void testOnItemPickup_NamespacedKeyNotPresent() {
        try ( // @formatter:off
            var languageConfigFile = Modules.LANGUAGE.makeTempConfig(LANGUAGE_CONFIG_CONTENT);
            var enTranslationFile  = Modules.LANGUAGE.makeTempResource("en.yml", EN_LANGUAGE_FILE_CONTENT)
        ) { // @formatter:on
            var actualLanguage = Modules.LANGUAGE.Actual(mockJavaPlugin, dependenciesMap);
            dependenciesMap.put(ILanguage.class, actualLanguage);

            assertNotNull(actualLanguage.getConfig(), "Failed to load config file");

            var config = mockFeatherEconomy.getConfig();
            when(config.getString("banknote.key")).thenReturn("banknote_key");

            listenerInstance.onItemPickup(mockEvent);

            verify(mockItemStack).getItemMeta();
        }
    }

    @Test
    public void testOnItemPickup_Success() {
        try ( // @formatter:off
            var languageConfigFile = Modules.LANGUAGE.makeTempConfig(LANGUAGE_CONFIG_CONTENT);
            var enTranslationFile  = Modules.LANGUAGE.makeTempResource("en.yml", EN_LANGUAGE_FILE_CONTENT)
        ) { // @formatter:on
            var actualLanguage = Modules.LANGUAGE.Actual(mockJavaPlugin, dependenciesMap);
            dependenciesMap.put(ILanguage.class, actualLanguage);

            assertNotNull(actualLanguage.getConfig(), "Failed to load config file");

            var config = mockFeatherEconomy.getConfig();
            when(config.getString("banknote.key")).thenReturn("banknote_key");

            when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockPersistentDataContainer);
            when(mockPersistentDataContainer.has(any(org.bukkit.NamespacedKey.class)))
                    .thenReturn(true);

            when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                    .thenReturn(100.0);

            PlayerModel playerModel = new PlayerModel();
            playerModel.language = "en";
            when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

            listenerInstance.onItemPickup(mockEvent);

            verify(mockItemStack).getItemMeta();
            verify(mockItemStack).setItemMeta(any());
        }
    }
}
