/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BalanceCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit BalanceCommand#0.8
 * @description Unit tests for BalanceCommand
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import static dev.defaultybuf.feathercore.modules.common.Modules.injectAs;
import static dev.defaultybuf.feathercore.modules.common.Modules.withResources;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.modules.common.CommandTestMocker;
import dev.defaultybuf.feathercore.modules.common.Modules;
import dev.defaultybuf.feathercore.modules.common.Resource;
import dev.defaultybuf.feathercore.modules.common.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;
import net.milkbowl.vault.economy.Economy;

class BalanceCommandTest extends CommandTestMocker<BalanceCommand> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
                    "  command:\n" +
                    "    no-permission: '&cYou do not have permission to execute this command.'\n" +
                    "    invalid: '&cInvalid command usage.'\n" +
                    "    players-only: '&cOnly players can execute this command.'\n" +
                    "  not-player: '&c{0} is not a player.'\n" +
                    "economy:\n" +
                    "  balance:\n" +
                    "    self: '&7Your balance: &e{0}'\n" +
                    "    other: '&7{0}&7''s balance: &e{1}'\n" +
                    "    usage: '&cUsage: /balance [player]'\n" +
                    "  banknote:\n" +
                    "    display-name: '&7Banknote'\n" +
                    "    lore:\n" +
                    "      - '&7Banknote value: &e{0}'";

    @Mock CommandSender mockSender;
    @Mock Player mockPlayer;
    @Mock OfflinePlayer mockOfflinePlayer;

    PlayerModel playerModel;
    IFeatherEconomy mockFeatherEconomy;
    IPlayersData mockPlayersData;

    TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<BalanceCommand> getCommandClass() {
        return BalanceCommand.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        mockFeatherEconomy = Modules.ECONOMY.Mock();

        var config = mockFeatherEconomy.getConfig();
        Mockito.lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        Mockito.lenient().when(mockFeatherEconomy.getEconomy()).thenReturn(mock(Economy.class));

        mockPlayersData = Modules.PLAYERS_DATA.Mock();

        return List.of(Pair.of(IFeatherEconomy.class, mockFeatherEconomy),
                Pair.of(IPlayersData.class, mockPlayersData));
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
        Mockito.lenient().when(mockSender.hasPermission("feathercore.economy.general.balance"))
                .thenReturn(true);
        Mockito.lenient().when(mockPlayer.hasPermission("feathercore.economy.general.balance"))
                .thenReturn(true);

        playerModel = new PlayerModel();
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockSender.hasPermission("feathercore.economy.general.balance")).thenReturn(false);

        var commandData = new BalanceCommand.CommandData(BalanceCommand.CommandType.SELF, null);
        var result = commandInstance.hasPermission(mockSender, commandData);

        verify(mockSender).sendMessage(anyString());
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithPermission() {
        var commandData = new BalanceCommand.CommandData(BalanceCommand.CommandType.SELF, null);
        var result = commandInstance.hasPermission(mockSender, commandData);

        assertTrue(result);
    }

    @Test
    void testExecute_Self() {
        var commandData = new BalanceCommand.CommandData(BalanceCommand.CommandType.SELF, null);

        when(mockFeatherEconomy.getEconomy().getBalance(mockPlayer)).thenReturn(100.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");

        playerModel.language = "en";
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

        commandInstance.execute(mockPlayer, commandData);

        verify(mockFeatherEconomy.getEconomy()).getBalance(mockPlayer);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_Other() {
        var commandData =
                new BalanceCommand.CommandData(BalanceCommand.CommandType.OTHER, mockOfflinePlayer);
        when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(200.0);
        when(mockFeatherEconomy.getEconomy().format(200.0)).thenReturn("200.0");
        when(mockOfflinePlayer.getName()).thenReturn("otherPlayer");

        commandInstance.execute(mockSender, commandData);

        verify(mockFeatherEconomy.getEconomy()).getBalance(mockOfflinePlayer);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_Self() {
        var args = new String[] {};
        var commandData = commandInstance.parse(mockPlayer, args);

        assertEquals(BalanceCommand.CommandType.SELF, commandData.commandType());
        assertNull(commandData.other());
    }

    @Test
    void testParse_SelfConsole() {
        var args = new String[] {};

        ConsoleCommandSender mockConsole = mock(ConsoleCommandSender.class);

        var commandData = commandInstance.parse(mockConsole, args);

        assertNull(commandData);
        verify(mockConsole).sendMessage(anyString());
    }

    @Test
    void testParse_Other() {
        var args = new String[] {"otherPlayer"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("otherPlayer"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            var commandData = commandInstance.parse(mockPlayer, args);

            assertEquals(BalanceCommand.CommandType.OTHER, commandData.commandType());
            assertEquals(mockOfflinePlayer, commandData.other());
        }
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"invalidPlayer"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("invalidPlayer"))
                    .thenReturn(mockOfflinePlayer);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_InvalidMultipleArguments() {
        var args = new String[] {"invalidPlayer1", "invalidPlayer2"};
        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testOnTabComplete_ArgsOnlinePlayer() {
        var args = new String[] {"oth"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockPlayer));
            when(mockPlayer.getName()).thenReturn("otherPlayer");

            var completions = commandInstance.onTabComplete(args);

            assertEquals(1, completions.size());
            assertTrue(completions.contains("otherPlayer"));
        }
    }

    @Test
    void testOnTabComplete_ArgsNotOnlinePlayer() {
        var args = new String[] {"player"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockPlayer));
            when(mockPlayer.getName()).thenReturn("otherPlayer");

            var completions = commandInstance.onTabComplete(args);

            assertTrue(completions.isEmpty());
        }
    }

    @Test
    void testOnTabComplete_NoArgs() {
        var args = new String[] {};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            Player mockPlayer2 = mock(Player.class);

            when(mockPlayer.getName()).thenReturn("otherPlayer1");
            when(mockPlayer2.getName()).thenReturn("otherPlayer2");

            mockedBukkit.when(Bukkit::getOnlinePlayers)
                    .thenReturn(List.of(mockPlayer, mockPlayer2));

            var completions = commandInstance.onTabComplete(args);

            assertEquals(2, completions.size());
            assertTrue(completions.contains("otherPlayer1"));
            assertTrue(completions.contains("otherPlayer2"));
        }
    }
}
