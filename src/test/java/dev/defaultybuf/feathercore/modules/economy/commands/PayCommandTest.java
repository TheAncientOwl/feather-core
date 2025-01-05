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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherCommandTest;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.utils.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import net.milkbowl.vault.economy.Economy;

class PayCommandTest extends FeatherCommandTest<PayCommand> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  command:\n" +
            "    no-permission: '&cYou do not have permission to execute this command.'\n" +
            "    invalid: '&cInvalid command usage.'\n" +
            "    players-only: '&cOnly players can execute this command.'\n" +
            "  not-player: '&c{0} is not a player.'\n" +
            "  not-online-player: '&c{0} is not online.'\n" +
            "  not-valid-number: '&cNot valid number'\n" +
            "economy:\n" +
            "  paytoggle:\n" +
            "    error:\n" +
            "      does-not-accept: '&c{player} does not accept payments.'\n" +
            "  pay:\n" +
            "    error:\n" +
            "      usage: 'Usage'\n" +
            "      min-amount: 'You have to pay at least {0}$'\n" +
            "      not-enough-funds: '&cYou do not have enough funds to send {0}.'\n" +
            "      balance-exceeds: 'The balance would exced max {0}'\n" +
            "    success:\n" +
            "      send: 'You have sent {amount} to {player}.'\n" +
            "      receive: 'You have received {amount} from {player}.'\n" +
            "    toggle-not-accepting: '{player} is not accepting payments.'\n" +
            "    balance-exceeds: 'You cannot send more than {amount}.'\n" +
            "    min-amount: 'You must send at least {amount}.'\n" +
            "    no-funds: 'You do not have enough funds to send {amount}.'\n" +
            "    usage: 'Usage: /pay <player> <amount>'";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock CommandSender mockSender;
    @Mock OfflinePlayer mockOfflinePlayer;

    @MockedModule(of = Module.Economy) IFeatherEconomy mockFeatherEconomy;
    @MockedModule(of = Module.PlayersData) IPlayersData mockPlayersData;

    @ActualModule(
            of = Module.Language,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    @TestField PlayerModel playerModel;

    @Override
    protected Class<PayCommand> getCommandClass() {
        return PayCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockSender.hasPermission("feathercore.economy.general.pay"))
                .thenReturn(true);
        lenient().when(mockPlayer.hasPermission("feathercore.economy.general.pay"))
                .thenReturn(true);
        lenient().when(mockFeatherEconomy.getEconomy()).thenReturn(mock(Economy.class));

        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockSender.hasPermission("feathercore.economy.general.pay")).thenReturn(false);

        var commandData = new PayCommand.CommandData(null, 0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        assertFalse(result);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testHasPermission_WithPermission() {
        when(mockSender.hasPermission("feathercore.economy.general.pay")).thenReturn(true);

        var commandData = new PayCommand.CommandData(null, 0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        assertTrue(result);
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {"player", "100"};

        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"player"};

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ReceiverNotOnline() {
        var args = new String[] {"player", "100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ReceiverNotAcceptingPayments() {
        playerModel.acceptsPayments = false;
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);
        when(mockOfflinePlayer.isOnline()).thenReturn(true);
        when(mockOfflinePlayer.getName()).thenReturn("player");
        when(mockPlayersData.getPlayerModel(mockOfflinePlayer)).thenReturn(playerModel);

        var args = new String[] {"player", "100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        when(mockFeatherEconomy.getEconomy().has(eq((OfflinePlayer) mockPlayer), anyDouble()))
                .thenReturn(true);
        when(mockFeatherEconomy.getEconomy().getBalance(mockReceiver)).thenReturn(10.0);

        when(mockFeatherEconomy.getConfig().getDouble("minimum-pay-amount")).thenReturn(1.0);
        when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000000.0);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            assertEquals(mockReceiver, commandData.receiver());
            assertEquals(100.0, commandData.amount());
        }
    }

    @Test
    void testParse_ValidArguments_NotEnoughFunds() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        when(mockFeatherEconomy.getEconomy().has(eq((OfflinePlayer) mockPlayer), anyDouble()))
                .thenReturn(false);

        when(mockFeatherEconomy.getConfig().getDouble("minimum-pay-amount")).thenReturn(1.0);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_NewBalanceExceedsMax() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        when(mockFeatherEconomy.getEconomy().has(eq((OfflinePlayer) mockPlayer), anyDouble()))
                .thenReturn(true);
        when(mockFeatherEconomy.getEconomy().getBalance(mockReceiver)).thenReturn(10.0);

        when(mockFeatherEconomy.getConfig().getDouble("minimum-pay-amount")).thenReturn(1.0);
        when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(50.0);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_AmountLessThanMin() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        when(mockFeatherEconomy.getConfig().getDouble("minimum-pay-amount")).thenReturn(10.0);

        var args = new String[] {"player", "1"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_InvalidAmount() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        var args = new String[] {"player", "invalid-amount"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_NoPlayerModel() {
        var mockReceiver = mock(OfflinePlayer.class);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer, never()).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_ReceiverNotOnline() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = true;
        recieverModel.language = "en";

        when(mockReceiver.getName()).thenReturn("player");
        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(false);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_ReceiverNotAcceptingPayments_NotOverride() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = false;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);
        when(mockPlayer.hasPermission("feathercore.economy.general.pay.override"))
                .thenReturn(false);

        when(mockReceiver.isOnline()).thenReturn(true);
        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.getName()).thenReturn("player");

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNull(commandData);
            verify(mockPlayer).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_ReceiverNotAcceptingPayments_Override() {
        var mockReceiver = mock(OfflinePlayer.class);
        var recieverModel = new PlayerModel();
        recieverModel.acceptsPayments = false;
        recieverModel.language = "en";

        when(mockPlayersData.getPlayerModel(mockReceiver)).thenReturn(recieverModel);

        when(mockPlayer.hasPermission("feathercore.economy.general.pay.override"))
                .thenReturn(true);

        when(mockReceiver.hasPlayedBefore()).thenReturn(true);
        when(mockReceiver.isOnline()).thenReturn(true);

        when(mockFeatherEconomy.getEconomy().has(eq((OfflinePlayer) mockPlayer), anyDouble()))
                .thenReturn(true);
        when(mockFeatherEconomy.getEconomy().getBalance(mockReceiver)).thenReturn(10.0);

        when(mockFeatherEconomy.getConfig().getDouble("minimum-pay-amount")).thenReturn(1.0);
        when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000000.0);

        var args = new String[] {"player", "100"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockReceiver);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertNotNull(commandData);
            assertEquals(mockReceiver, commandData.receiver());
            assertEquals(100.0, commandData.amount());
        }
    }

    @Test
    void testExecute() {
        var mockPlayer2 = mock(Player.class);
        var playerModel2 = new PlayerModel();
        playerModel2.language = "en";

        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");

        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
        when(mockPlayersData.getPlayerModel(mockPlayer2)).thenReturn(playerModel2);

        when(mockPlayer2.getName()).thenReturn("player");
        when(mockPlayer.getName()).thenReturn("sender");

        var commandData = new PayCommand.CommandData(mockPlayer2, 100.0);
        commandInstance.execute(mockPlayer, commandData);

        verify(mockFeatherEconomy.getEconomy()).withdrawPlayer(mockPlayer, 100.0);
        verify(mockFeatherEconomy.getEconomy()).depositPlayer(mockPlayer2, 100.0);
        verify(mockPlayer).sendMessage(anyString());
        verify(mockPlayer2).sendMessage(anyString());
    }

    @ParameterizedTest
    @MethodSource("getTabCompleteArgs")
    void testOnTabComplete(String[] args, List<String> onlinePlayers, List<String> expected) {
        try (var mockedStringUtils = mockStatic(StringUtils.class)) {
            mockedStringUtils.when(StringUtils::getOnlinePlayers).thenReturn(onlinePlayers);
            mockedStringUtils.when(() -> StringUtils.filterStartingWith(any(), any()))
                    .thenCallRealMethod();

            var completions = commandInstance.onTabComplete(args);

            assertEquals(expected, completions);
        }
    }

    static Stream<Arguments> getTabCompleteArgs() {
        return Stream.of(
                Arguments.of(
                        new String[] {""},
                        List.of("player1", "player2"),
                        List.of("player1", "player2")),
                Arguments.of(
                        new String[] {"p"},
                        List.of("player1", "player2"),
                        List.of("player1", "player2")),
                Arguments.of(
                        new String[] {"player1"},
                        List.of("player1", "player2"),
                        List.of("player1")),
                Arguments.of(
                        new String[] {"player3"},
                        List.of("player1", "player2"),
                        List.of()),
                Arguments.of(
                        new String[] {"player", "amount"},
                        List.of("player1", "player2"),
                        List.of("amount")),
                Arguments.of(
                        new String[] {"player1", ""},
                        List.of("player1", "player2"),
                        List.of("amount")),
                Arguments.of(
                        new String[] {"player2", "a"},
                        List.of("player1", "player2"),
                        List.of("amount")),
                Arguments.of(
                        new String[] {},
                        List.of(),
                        List.of()),
                Arguments.of(
                        new String[] {"player1", "amount", "random-string"},
                        List.of("player1"),
                        List.of()));
    }
}
