/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PayCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit PayCommand#0.10
 * @description Unit tests for PayCommand
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherCommandTest;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.utils.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import net.milkbowl.vault.economy.Economy;

class WithdrawCommandTest extends FeatherCommandTest<WithdrawCommand> {
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
            "economy:\n" +
            "  banknote:\n" +
            "    invalid-material: 'Invalid material'\n" +
            "    display-name: 'Banknote'\n" +
            "    lore:\n" +
            "      - 'lore-line'\n" +
            "    lang: 'en'\n" +
            "  withdraw:\n" +
            "    success: 'Success'\n" +
            "    error:\n" +
            "      usage: 'Usage'\n" +
            "      minimum-value: 'Min value: {0}$'\n" +
            "      not-enough-space: 'Not enough space'\n" +
            "      not-enough-funds: 'Not enough funds'\n";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock ItemMeta mockItemMeta;
    @Mock ItemStack mockItemStack;
    @Mock PlayerInventory mockInventory;
    @Mock CommandSender mockSender;
    @Mock ItemFactory mockItemFactory;

    @MockedModule(of = Module.Economy) IFeatherEconomy mockFeatherEconomy;
    @MockedModule(of = Module.PlayersData) IPlayersData mockPlayersData;

    @ActualModule(
            of = Module.Language,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    ItemStack[] mocksInventoryItems;
    PlayerModel playerModel;

    @Override
    protected Class<WithdrawCommand> getCommandClass() {
        return WithdrawCommand.class;
    }

    @Override
    protected void setUp() {
        var config = mockFeatherEconomy.getConfig();
        lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        lenient().when(mockFeatherEconomy.getEconomy()).thenReturn(mock(Economy.class));

        lenient().when(mockSender.hasPermission("feathercore.economy.general.withdraw"))
                .thenReturn(true);
        lenient().when(mockPlayer.hasPermission("feathercore.economy.general.withdraw"))
                .thenReturn(true);

        lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        lenient().when(mockPlayer.getInventory()).thenReturn(mockInventory);

        playerModel = new PlayerModel();
        playerModel.language = "en";
        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

        lenient().when(mockServer.getItemFactory()).thenReturn(mockItemFactory);

        mocksInventoryItems = new ItemStack[36];
        for (int index = 0; index < 36; ++index) {
            mocksInventoryItems[index] = mock(ItemStack.class);
        }
        lenient().when(mockInventory.getContents()).thenReturn(mocksInventoryItems);
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockPlayer.hasPermission("feathercore.economy.general.withdraw")).thenReturn(false);

        var commandData = new WithdrawCommand.CommandData(mockItemStack, 100.0);
        var result = commandInstance.hasPermission(mockPlayer, commandData);

        assertFalse(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testHasPermission_WithPermission() {
        var commandData = new WithdrawCommand.CommandData(mockItemStack, 100.0);
        var result = commandInstance.hasPermission(mockPlayer, commandData);

        assertTrue(result);
    }

    @Test
    void testExecute() {
        var commandData = new WithdrawCommand.CommandData(mockItemStack, 100.0);
        when(mockFeatherEconomy.getEconomy().getBalance(mockPlayer)).thenReturn(200.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");
        when(mockFeatherEconomy.getEconomy().format(200.0)).thenReturn("200.0");

        commandInstance.execute(mockPlayer, commandData);

        verify(mockFeatherEconomy.getEconomy()).withdrawPlayer(mockPlayer, 100.0);
        verify(mockPlayer.getInventory()).addItem(mockItemStack);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {"100", "1"};
        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"invalid", "1"};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArgumentsCount() {
        var args = new String[] {"invalid", "1", "some-arg"};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            mocksInventoryItems[5] = null;

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            assertEquals(100.0, commandData.withdrawValue());
        }
    }

    @Test
    void testParse_ValidArguments_StackOnTop() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            var banknoteItemStack = mocksInventoryItems[5];
            when(banknoteItemStack.isSimilar(any(ItemStack.class))).thenReturn(true);
            when(banknoteItemStack.getAmount()).thenReturn(31);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            assertEquals(100.0, commandData.withdrawValue());
        }
    }

    @Test
    void testParse_ValidArguments_StackOnTopButNotSimilar() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            var banknoteItemStack = mocksInventoryItems[5];
            when(banknoteItemStack.isSimilar(any(ItemStack.class))).thenReturn(false);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_CannotStackOnTop() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            var banknoteItemStack = mocksInventoryItems[5];
            when(banknoteItemStack.isSimilar(any(ItemStack.class))).thenReturn(true);
            when(banknoteItemStack.getAmount()).thenReturn(64);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
        }
    }

    @Test
    void testParse_ValidArguments_LoreAmount() throws IOException {
        actualLanguage.module().getConfig().setStringList("economy.banknote.lore",
                List.of("line1", "{amount}"));
        actualLanguage.module().getConfig().saveConfig();

        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            mocksInventoryItems[5] = null;

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            assertEquals(100.0, commandData.withdrawValue());
        }
    }

    @Test
    void testParse_ValidArguments_InvalidBanknoteMaterial() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material"))
                .thenReturn("INVALID-PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            mocksInventoryItems[5] = null;

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_CantAddBanknote() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(true);
        when(mockFeatherEconomy.getConfig().getString("banknote.material")).thenReturn("PAPER");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_WithdrawLessThanMin() {
        var args = new String[] {"1", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(10.0);
        when(mockFeatherEconomy.getEconomy().format(10.0)).thenReturn("10.0");

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            mocksInventoryItems[5] = null;

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_InvalidBanknotesCount() {
        var args = new String[] {"100", "invalid-banknotes-count"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(mockItemFactory);

            mocksInventoryItems[5] = null;

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_InsufficientFunds() {
        var args = new String[] {"100", "1"};
        when(mockFeatherEconomy.getConfig().getDouble("banknote.minimum-value")).thenReturn(1.0);
        when(mockFeatherEconomy.getEconomy().has(mockPlayer, 100.0)).thenReturn(false);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnTabComplete() {
        var args = new String[] {"100"};
        var completions = commandInstance.onTabComplete(args);

        assertEquals(List.of("banknote-value"), completions);
    }

    @Test
    void testOnTabComplete_SecondArgument() {
        var args = new String[] {"100", "1"};
        var completions = commandInstance.onTabComplete(args);

        assertEquals(List.of("amount"), completions);
    }

    @Test
    void testOnTabComplete_ThirdArgument() {
        var args = new String[] {"100", "1", "some-argument"};
        var completions = commandInstance.onTabComplete(args);

        assertTrue(completions.isEmpty());
    }
}
