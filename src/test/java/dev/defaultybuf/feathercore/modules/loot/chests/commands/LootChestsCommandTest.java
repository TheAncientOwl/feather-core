/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestsCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit LootChestsCommand#0.9
 * @description Unit tests for LootChestsCommand
 */

package dev.defaultybuf.feathercore.modules.loot.chests.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.api.configuration.IConfigSection;
import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.common.mockers.CommandTestMocker;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.utils.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.loot.chests.interfaces.ILootChests;

class LootChestsCommandTest extends CommandTestMocker<LootChestsCommand> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  no-permission: '&cYou do not have permission to execute this command.'\n" +
            "  command:\n" +
            "    invalid: '&cInvalid command usage.'\n" +
            "    players-only: '&cOnly players can execute this command.'\n" +
            "  not-player: '&c{0} is not a player.'\n" +
            "  not-valid-number: '&cNot valid number'\n" +
            "loot-chests:\n" +
            "  usage: 'Usage'\n" +
            "  not-a-chest: '&c{0} is not a chest.'\n" +
            "  not-a-registered-chest: '&c{0} is not a registered chest.'\n" +
            "  set-success: '&aSuccessfully set chest at {0} to type {1}.'\n" +
            "  unset-success: '&aSuccessfully unset chest at {0} of type {1}.'\n" +
            "  create-success: '&aSuccessfully created chest of type {0}.'\n" +
            "  delete-success: '&aSuccessfully deleted chest of type {0}.'\n" +
            "  locations: '&aChest locations for type {0}: {1}'\n" +
            "  info: '&aChest type: {0}'\n";
    // @formatter:on

    @Mock Block mockBlock;
    @Mock Chest mockChest;
    @Mock Player mockPlayer;
    @Mock Location mockLocation;
    @Mock Inventory mockInventory;
    @Mock CommandSender mockSender;
    @Mock BlockState mockBlockState;
    @Mock IConfigSection mockConfigSection;

    @MockedModule(of = Module.LootChests) ILootChests mockLootChests;
    @MockedModule(of = Module.PlayersData) IPlayersData mockPlayersData;

    @ActualModule(of = Module.Language, resources = {
            @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
            @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
    }) TempModule<LanguageManager> actualLanguage;

    @TestField PlayerModel playerModel;

    @Override
    protected Class<LootChestsCommand> getCommandClass() {
        return LootChestsCommand.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
        lenient().when(mockSender.hasPermission("feathercore.lootchests")).thenReturn(true);
        lenient().when(mockPlayer.hasPermission("feathercore.lootchests")).thenReturn(true);
        lenient().when(mockBlock.getState()).thenReturn(mockChest);

        lenient().when(mockLocation.toString()).thenReturn("world,0,64,0");
        lenient().when(mockChest.getLocation()).thenReturn(mockLocation);
        lenient().when(mockChest.getInventory()).thenReturn(mockInventory);
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockSender.hasPermission("feathercore.lootchests")).thenReturn(false);

        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.SET, "testType", null, 0, mockChest);
        var result = commandInstance.hasPermission(mockSender, data);

        assertFalse(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testHasPermission_WithPermission() {
        when(mockSender.hasPermission("feathercore.lootchests")).thenReturn(true);

        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.SET, "testType", null, 0, mockChest);
        var result = commandInstance.hasPermission(mockSender, data);

        assertTrue(result);
    }

    @Test
    void testParse_NoArgs() {
        var args = new String[] {};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidCommand() {
        var args = new String[] {"invalid"};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_SetCommand() {
        var args = new String[] {"set", "testType"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockLootChests.isChestType("testType")).thenReturn(true);
        when(mockBlock.getType()).thenReturn(Material.CHEST);

        var result = commandInstance.parse(mockPlayer, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.SET, result.commandType());
        assertEquals("testType", result.chestType());
        assertEquals(mockChest, result.chest());
    }

    @Test
    void testParse_SetCommand_NotChest() {
        var args = new String[] {"set", "testType"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockLootChests.isChestType("testType")).thenReturn(true);
        when(mockBlock.getType()).thenReturn(Material.ACACIA_BOAT);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_SetCommand_SenderNotPlayer() {
        var args = new String[] {"set", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(true);

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_SetCommand_NotChestType() {
        var args = new String[] {"set", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(false);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_SetCommand_InvalidArgsCount() {
        var args = new String[] {"set"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_CreateCommand() {
        var args = new String[] {"create", "testType", "Test Chest", "3600"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.ACACIA_BOAT);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_CreateCommand_SenderNotPlayer() {
        var args = new String[] {"create", "testType", "Test Chest", "3600"};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_CreateCommand_InvalidCooldown() {
        var args = new String[] {"create", "testType", "Test Chest", "invalid-cooldown"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_CreateCommand_InvalidArgsLess() {
        var args = new String[] {"create", "testType", "Test Chest"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_CreateCommand_InvalidArgsMore() {
        var args = new String[] {"create", "testType", "Test Chest", "3600", "more-args"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_DeleteCommand() {
        var args = new String[] {"delete", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(true);

        var result = commandInstance.parse(mockSender, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.DELETE, result.commandType());
        assertEquals("testType", result.chestType());
    }

    @Test
    void testParse_DeleteCommand_NotChestType() {
        var args = new String[] {"delete", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(false);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_DeleteCommand_InvalidArgsCount() {
        var args = new String[] {"delete"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_LocationsCommand() {
        var args = new String[] {"locations", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(true);

        var result = commandInstance.parse(mockSender, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.LOCATIONS, result.commandType());
        assertEquals("testType", result.chestType());
    }

    @Test
    void testParse_LocationsCommand_NotChestType() {
        var args = new String[] {"locations", "testType"};
        when(mockLootChests.isChestType("testType")).thenReturn(false);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_LocationsCommand_InvalidArgsCount() {
        var args = new String[] {"locations"};

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_InfoCommand() {
        var args = new String[] {"info"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.ACACIA_BOAT);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_InfoCommand_SenderNotPlayer() {
        var args = new String[] {"info"};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_UnsetCommand() {
        var args = new String[] {"unset"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.CHEST);
        when(mockLootChests.getChestType(anyString())).thenReturn("testType");

        var result = commandInstance.parse(mockPlayer, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.UNSET, result.commandType());
        assertEquals("testType", result.chestType());
        assertEquals(mockChest, result.chest());
    }

    @Test
    void testParse_UnsetCommand_NotChest() {
        var args = new String[] {"unset"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.ACACIA_BOAT);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_UnsetCommand_SenderNotPlayer() {
        var args = new String[] {"unset"};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testExecute_SetCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.SET, "testType", null, 0, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockLootChests).setChest(anyString(), eq("testType"));
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_CreateCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.CREATE, "testType", "Test Chest", 3600L, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockLootChests).createChest(eq("testType"), eq("Test Chest"), eq(3600L),
                any(Inventory.class));
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_DeleteCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.DELETE, "testType", null, 0, null);
        commandInstance.execute(mockSender, data);

        verify(mockLootChests).deleteChest("testType");
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testExecute_LocationsCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.LOCATIONS, "testType", null, 0, null);
        when(mockLootChests.getChestLocations("testType"))
                .thenReturn(List.of("world,0,64,0", "world,1,64,0"));

        commandInstance.execute(mockSender, data);

        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testExecute_LocationsCommand_NoLocation() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.LOCATIONS, "testType", null, 0, null);
        when(mockLootChests.getChestLocations("testType")).thenReturn(List.of());

        commandInstance.execute(mockSender, data);

        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testExecute_InfoCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.INFO, "testType", null, 0, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_InfoCommand_NullChestType() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.INFO, null, null, 0, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_UnsetCommand() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.UNSET, "testType", null, 0, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockLootChests).unsetChest(anyString());
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_UnsetCommand_NullType() {
        var data = new LootChestsCommand.CommandData(
                LootChestsCommand.CommandType.UNSET, null, null, 0, mockChest);
        commandInstance.execute(mockPlayer, data);

        verify(mockLootChests, never()).unsetChest(anyString());
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ChestTypeNullAndChestNotNull() {
        var args = new String[] {"info"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.CHEST);
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");

        var result = commandInstance.parse(mockPlayer, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.INFO, result.commandType());
        assertEquals("testType", result.chestType());
        assertEquals(mockChest, result.chest());
    }

    @Test
    void testParse_ChestTypeNotNullAndChestNotNull() {
        var args = new String[] {"info"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.CHEST);
        when(mockLootChests.getChestType("world,0,64,0")).thenReturn("testType");

        var result = commandInstance.parse(mockPlayer, args);

        assertNotNull(result);
        assertEquals(LootChestsCommand.CommandType.INFO, result.commandType());
        assertEquals("testType", result.chestType());
        assertEquals(mockChest, result.chest());
    }

    @Test
    void testParse_ChestTypeNullAndChestNull() {
        var args = new String[] {"info"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.AIR);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ChestTypeNotNullAndChestNull() {
        var args = new String[] {"info"};
        when(mockPlayer.getTargetBlock((Set<Material>) null, 5)).thenReturn(mockBlock);
        when(mockBlock.getType()).thenReturn(Material.AIR);

        var result = commandInstance.parse(mockPlayer, args);

        assertNull(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @ParameterizedTest
    @MethodSource("provideTabCompleteData")
    void testOnTabComplete(String[] args, List<String> expectedCompletions) {
        lenient().when(mockLootChests.getConfig().getConfigurationSection("chests"))
                .thenReturn(mockConfigSection);
        lenient().when(mockConfigSection.getKeys(false)).thenReturn(Set.of("type1", "type2"));

        var completions = commandInstance.onTabComplete(args);

        assertEquals(expectedCompletions.size(), completions.size());
        assertEquals(new HashSet<>(expectedCompletions), new HashSet<>(completions));
    }

    static Stream<Arguments> provideTabCompleteData() {
        return Stream.of(
                Arguments.of(new String[] {}, List.of()),
                Arguments.of(
                        new String[] {""},
                        List.of("set", "unset", "create", "delete", "info", "locations")),
                Arguments.of(new String[] {"s"}, List.of("set")),
                Arguments.of(new String[] {"c"}, List.of("create")),
                Arguments.of(new String[] {"d"}, List.of("delete")),
                Arguments.of(new String[] {"i"}, List.of("info")),
                Arguments.of(new String[] {"l"}, List.of("locations")),
                Arguments.of(new String[] {"set", ""}, List.of("type1", "type2")),
                Arguments.of(new String[] {"create", ""}, List.of("type1", "type2")),
                Arguments.of(new String[] {"delete", ""}, List.of("type1", "type2")),
                Arguments.of(new String[] {"locations", ""}, List.of("type1", "type2")),
                Arguments.of(new String[] {"create", "type1", ""}, List.of("display-name")),
                Arguments.of(
                        new String[] {"create", "type1", "display-name", ""},
                        List.of("cooldown (seconds)")),
                Arguments.of(
                        new String[] {"create", "type1", "display-name", "", "some-other-arg"},
                        List.of()),
                Arguments.of(new String[] {"unknown", "arg"}, List.of()));
    }

}
