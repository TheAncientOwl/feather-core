/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DepositCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit DepositCommand#0.10
 * @description Unit tests for DepositCommand
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import static dev.defaultybuf.feathercore.modules.common.DependencyInjector.withResources;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.modules.common.CommandTestMocker;
import dev.defaultybuf.feathercore.modules.common.DependencyInjector;
import dev.defaultybuf.feathercore.modules.common.Resource;
import dev.defaultybuf.feathercore.modules.common.TempModule;
import dev.defaultybuf.feathercore.modules.common.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import net.milkbowl.vault.economy.Economy;

class DepositCommandTest extends CommandTestMocker<DepositCommand> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
                    "  command:\n" +
                    "    no-permission: '&cYou do not have permission to execute this command.'\n" +
                    "    invalid: '&cInvalid command usage.'\n" +
                    "    players-only: '&cOnly players can execute this command.'\n" +
                    "  not-player: '&c{0} is not a player.'\n" +
                    "  not-valid-number: '&cNot valid number'\n" +
                    "economy:\n" +
                    "  banknote:\n" +
                    "    error:\n" +
                    "      invalid: '&cInvalid banknote.'\n" +
                    "  deposit:\n" +
                    "    error:\n" +
                    "      invalid-amount: '&cInvalid amount.'\n" +
                    "      balance-exceeds: '&cYou cannot deposit more than {0}.'\n" +
                    "      negative-amount: '&cYou cannot deposit negative amounts.'\n" +
                    "    success: 'Deposit successful'";

    @Mock Player mockPlayer;
    @Mock ItemMeta mockItemMeta;
    @Mock ItemStack mockItemStack;
    @Mock CommandSender mockSender;
    @Mock PlayerInventory mockPlayerInventory;
    @Mock PersistentDataContainer mockPersistentDataContainer;

    @MockedModule(type = Module.PlayersData) IPlayersData mockPlayersData;
    @MockedModule(type = Module.Economy) IFeatherEconomy mockFeatherEconomy;

    @ActualModule TempModule<LanguageManager> actualLanguage;

    @TestField PlayerModel playerModel;

    @Override
    protected Class<DepositCommand> getCommandClass() {
        return DepositCommand.class;
    }

    @Override
    protected void injectActualModules() {
        var config = mockFeatherEconomy.getConfig();
        lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        lenient().when(mockFeatherEconomy.getEconomy()).thenReturn(mock(Economy.class));

        actualLanguage = DependencyInjector.Language.Actual(withResources(
                Resource.of("config.yml", LANGUAGE_CONFIG_CONTENT),
                Resource.of("en.yml", EN_LANGUAGE_FILE_CONTENT)));
    }

    @Override
    protected void setUp() {
        lenient().when(mockSender.hasPermission("feathercore.economy.general.deposit"))
                .thenReturn(true);
        lenient().when(mockPlayer.hasPermission("feathercore.economy.general.deposit"))
                .thenReturn(true);
        lenient().when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
        lenient().when(mockPlayerInventory.getItemInMainHand()).thenReturn(mockItemStack);
        lenient().when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        lenient().when(mockItemMeta.getPersistentDataContainer())
                .thenReturn(mockPersistentDataContainer);

        playerModel = new PlayerModel();
        playerModel.language = "en";
        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockSender.hasPermission("feathercore.economy.general.deposit")).thenReturn(false);

        var commandData = new DepositCommand.CommandData(mockItemStack, 1, 100.0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        verify(mockSender).sendMessage(anyString());
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithPermission() {
        var commandData = new DepositCommand.CommandData(mockItemStack, 1, 100.0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        assertTrue(result);
    }

    @Test
    void testExecute() {
        var commandData = new DepositCommand.CommandData(mockItemStack, 1, 100.0);
        when(mockFeatherEconomy.getEconomy().getBalance(mockPlayer)).thenReturn(100.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");

        commandInstance.execute(mockPlayer, commandData);

        verify(mockFeatherEconomy.getEconomy()).depositPlayer(mockPlayer, 100.0);
        verify(mockItemStack).setAmount(mockItemStack.getAmount() - 1);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {"1"};
        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArgument() {
        var args = new String[] {"invalid"};
        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"invalid1", "invalid2"};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments_NegativeAmount() {
        var args = new String[] {"-1"};
        when(mockPersistentDataContainer.has(any())).thenReturn(true);
        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments_NotExceds() {
        var args = new String[] {"1"};
        when(mockItemStack.getAmount()).thenReturn(1);
        when(mockPersistentDataContainer.has(any())).thenReturn(true);
        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);
        when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNotNull(commandData);
        assertEquals(1, commandData.banknotesCount());
        assertEquals(100.0, commandData.depositValue());
    }

    @Test
    void testParse_ValidArguments_NotExceds_MoreBanknotes() {
        var args = new String[] {"2"};
        when(mockItemStack.getAmount()).thenReturn(1);
        when(mockPersistentDataContainer.has(any())).thenReturn(true);
        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments_Exceds() {
        var args = new String[] {"1"};
        when(mockItemStack.getAmount()).thenReturn(1);
        when(mockPersistentDataContainer.has(any())).thenReturn(true);
        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);
        when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(10.0);
        when(mockFeatherEconomy.getEconomy().format(10.0)).thenReturn("10.0");

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_AmountNotNumber() {
        var args = new String[] {"nan"};
        when(mockPersistentDataContainer.has(any())).thenReturn(true);
        when(mockPersistentDataContainer.get(any(), eq(PersistentDataType.DOUBLE)))
                .thenReturn(100.0);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments_NotBanknote() {
        var args = new String[] {"1"};

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ItemInHandNull() {
        lenient().when(mockPlayerInventory.getItemInMainHand()).thenReturn(null);

        var args = new String[] {"1"};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ItemMetaNull() {
        lenient().when(mockItemStack.getItemMeta()).thenReturn(null);

        var args = new String[] {"1"};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnTabComplete_Args() {
        var args = new String[] {"am"};
        var completions = commandInstance.onTabComplete(args);

        assertEquals(1, completions.size());
        assertTrue(completions.contains("amount"));
    }

    @Test
    void testOnTabComplete_NoArgs() {
        var args = new String[] {};
        var completions = commandInstance.onTabComplete(args);

        assertEquals(1, completions.size());
        assertTrue(completions.contains("amount"));
    }
}
