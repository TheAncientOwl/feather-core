/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestOpenListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit LootChestOpenListener#0.8
 * @description Unit tests for LootChestOpenListener
 */

package dev.defaultybuf.feathercore.modules.loot.chests.listeners;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feather.toolkit.util.java.Clock;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.loot.chests.interfaces.ILootChests;

class LootChestOpenListenerTest extends FeatherListenerTest<LootChestOpenListener> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
     static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  command:\n" +
            "    no-permission: '&cYou do not have permission to execute this command.'\n" +
            "    invalid: '&cInvalid command usage.'\n" +
            "    players-only: '&cOnly players can execute this command.'\n" +
            "  not-player: '&c{0} is not a player.'\n" +
            "  not-valid-number: '&cNot valid number'\n" +
            "loot-chests:\n" +
            "  not-a-chest: '&c{0} is not a chest.'\n" +
            "  not-a-registered-chest: '&c{0} is not a registered chest.'\n" +
            "  set-success: '&aSuccessfully set chest at {0} to type {1}.'\n" +
            "  unset-success: '&aSuccessfully unset chest at {0} of type {1}.'\n" +
            "  create-success: '&aSuccessfully created chest of type {0}.'\n" +
            "  delete-success: '&aSuccessfully deleted chest of type {0}.'\n" +
            "  locations: '&aChest locations for type {0}: {1}'\n" +
            "  info: '&aChest type: {0}'\n" +
            "  cooldown: '&cYou must wait {0} before opening this chest again.'\n";
    // @formatter:on

    @Mock Chest mockChest;
    @Mock Block mockBlock;
    @Mock Player mockPlayer;
    @Mock Location mockLocation;
    @Mock PlayerInteractEvent mockEvent;

    @MockedModule IPlayersData mockPlayersData;
    @MockedModule ILootChests mockLootChests;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    PlayerModel playerModel;

    @Override
    protected Class<LootChestOpenListener> getListenerClass() {
        return LootChestOpenListener.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

        lenient().when(mockLocation.toString()).thenReturn("world,0,64,0");
        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockEvent.getClickedBlock()).thenReturn(mockBlock);
        lenient().when(mockBlock.getType()).thenReturn(Material.CHEST);
        lenient().when(mockBlock.getState()).thenReturn(mockChest);
        lenient().when(mockBlock.getLocation()).thenReturn(mockLocation);
        lenient().when(mockChest.getLocation()).thenReturn(mockLocation);
    }

    @Test
    void testOnChestOpen_BlockIsNull() {
        when(mockEvent.getClickedBlock()).thenReturn(null);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnChestOpen_NotAChest() {
        when(mockBlock.getType()).thenReturn(Material.STONE);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnChestOpen_NotRegisteredChest() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn(null);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnChestOpen_CooldownNotBypassed() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");
        when(mockLootChests.getOpenChestTime(mockPlayer, "world,0,64,0"))
                .thenReturn(Clock.currentTimeMillis() - 1000);
        when(mockLootChests.getConfig().getMillis("chests.testType.cooldown")).thenReturn(5000L);
        when(mockPlayer.hasPermission("feathercore.lootchests.bypass-cooldown")).thenReturn(false);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnChestOpen_CooldownBypassed() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");
        when(mockLootChests.getOpenChestTime(mockPlayer, "world,0,64,0"))
                .thenReturn(Clock.currentTimeMillis() - 1000);
        when(mockLootChests.getConfig().getMillis("chests.testType.cooldown")).thenReturn(5000L);
        when(mockPlayer.hasPermission("feathercore.lootchests.bypass-cooldown")).thenReturn(true);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockLootChests).openChest(eq(mockPlayer), eq("testType"), eq("world,0,64,0"),
                anyLong());
    }

    @Test
    void testOnChestOpen_NoCooldown() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");
        when(mockLootChests.getOpenChestTime(mockPlayer, "world,0,64,0")).thenReturn(null);
        when(mockLootChests.getConfig().getMillis("chests.testType.cooldown")).thenReturn(5000L);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockLootChests).openChest(eq(mockPlayer), eq("testType"), eq("world,0,64,0"),
                anyLong());
    }

    @Test
    void testOnChestOpen_CooldownNotExceeded() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");
        when(mockLootChests.getOpenChestTime(mockPlayer, "world,0,64,0"))
                .thenReturn(Clock.currentTimeMillis() - 3000);
        when(mockLootChests.getConfig().getMillis("chests.testType.cooldown")).thenReturn(5000L);
        when(mockPlayer.hasPermission("feathercore.lootchests.bypass-cooldown")).thenReturn(false);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockPlayer).sendMessage(anyString());
        verify(mockLootChests, never()).openChest(eq(mockPlayer), eq("testType"),
                eq("world,0,64,0"), anyLong());
    }

    @Test
    void testOnChestOpen_CooldownExceeded() {
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");
        when(mockLootChests.getOpenChestTime(mockPlayer, "world,0,64,0"))
                .thenReturn(Clock.currentTimeMillis() - 7000);
        when(mockLootChests.getConfig().getMillis("chests.testType.cooldown")).thenReturn(5000L);

        listenerInstance.onChestOpen(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockPlayer, never()).sendMessage(anyString());
    }
}
