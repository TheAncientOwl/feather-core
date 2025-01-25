/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file EcoCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @test_unit EcoCommand#0.10
 * @description Unit tests for EcoCommand
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

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.core.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.testing.core.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.core.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.core.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.core.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feathercore.common.FeatherCoreDependencyFactory;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import net.milkbowl.vault.economy.Economy;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class EcoCommandTest extends FeatherCommandTest<EcoCommand> {
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
            "  eco:\n" +
            "    usage: 'Usage'\n" +
            "    error:\n" +
            "      no-negative-amount: '&cYou cannot use negative amounts for {0}.'\n" +
            "      bounds-max: '&cYou cannot exceed the maximum balance of {0}.'\n" +
            "      bounds-min: '&cYou cannot go below the minimum balance of {0}.'\n" +
            "    success: 'Eco command executed successfully'\n" +
            "    cannot-negative-amounts: '&cYou cannot use negative amounts for {0}.'\n" +
            "    bounds:\n" +
            "      min: '0.0'\n" +
            "      max: '1000.0'\n";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock CommandSender mockSender;
    @Mock OfflinePlayer mockOfflinePlayer;

    @MockedModule IFeatherEconomy mockFeatherEconomy;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<EcoCommand> getCommandClass() {
        return EcoCommand.class;
    }

    @Override
    protected void setUp() {
        var config = mockFeatherEconomy.getConfig();
        lenient().when(config.getString("banknote.key")).thenReturn("banknote_key");

        lenient().when(mockSender.hasPermission("feathercore.economy.setup.eco")).thenReturn(true);

        lenient().when(mockFeatherEconomy.getEconomy()).thenReturn(mock(Economy.class));
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockSender.hasPermission("feathercore.economy.setup.eco")).thenReturn(false);

        var commandData = new EcoCommand.CommandData(mockOfflinePlayer, EcoCommand.CommandType.GIVE,
                0, 100.0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        verify(mockSender).sendMessage(any(String.class));
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithPermission() {
        var commandData = new EcoCommand.CommandData(mockOfflinePlayer, EcoCommand.CommandType.GIVE,
                0, 100.0);
        var result = commandInstance.hasPermission(mockSender, commandData);

        assertTrue(result);
    }

    @Test
    void testExecute_Give() {
        var commandData = new EcoCommand.CommandData(mockOfflinePlayer, EcoCommand.CommandType.GIVE,
                0, 100.0);
        when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");
        when(mockFeatherEconomy.getEconomy().format(0.0)).thenReturn("0.0");
        when(mockOfflinePlayer.getName()).thenReturn("player");

        commandInstance.execute(mockSender, commandData);

        verify(mockFeatherEconomy.getEconomy()).depositPlayer(mockOfflinePlayer, 100.0);
        verify(mockSender).sendMessage(any(String.class));
    }

    @Test
    void testExecute_Take() {
        var commandData = new EcoCommand.CommandData(mockOfflinePlayer, EcoCommand.CommandType.TAKE,
                100.0, 50.0);
        when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");
        when(mockOfflinePlayer.getName()).thenReturn("player");

        commandInstance.execute(mockSender, commandData);

        verify(mockFeatherEconomy.getEconomy()).withdrawPlayer(mockOfflinePlayer, 50.0);
        verify(mockSender).sendMessage(any(String.class));
    }

    @Test
    void testExecute_Set() {
        var commandData = new EcoCommand.CommandData(mockOfflinePlayer, EcoCommand.CommandType.SET,
                100.0, 200.0);
        when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
        when(mockFeatherEconomy.getEconomy().format(100.0)).thenReturn("100.0");

        when(mockOfflinePlayer.getName()).thenReturn("player");

        commandInstance.execute(mockSender, commandData);

        verify(mockFeatherEconomy.getEconomy()).withdrawPlayer(mockOfflinePlayer, 100.0);
        verify(mockFeatherEconomy.getEconomy()).depositPlayer(mockOfflinePlayer, 200.0);
        verify(mockSender).sendMessage(any(String.class));
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {"give", "player", "100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(any(String.class));
        }
    }

    @Test
    void testParse_InvalidCommandType() {
        var args = new String[] {"invalid", "player", "100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockPlayer);
            when(mockPlayer.hasPlayedBefore()).thenReturn(true);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"invalid"};
        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(any(String.class));
    }

    @Test
    void testParse_ValidArguments_Give() {
        var args = new String[] {"give", "player", "100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);

            var commandData = commandInstance.parse(mockSender, args);

            assertNotNull(commandData);
            assertEquals(EcoCommand.CommandType.GIVE, commandData.commandType());
            assertEquals(100.0, commandData.amount());
        }
    }

    @Test
    void testParse_ValidArguments_GiveOverMax() {
        var args = new String[] {"give", "player", "200000"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);
            when(mockFeatherEconomy.getEconomy().format(1000.0)).thenReturn("1000.0");

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_GiveNegative() {
        var args = new String[] {"give", "player", "-100"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_Give_InvalidAmount() {
        var args = new String[] {"give", "player", "invalid-amount"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_Take() {
        var args = new String[] {"take", "player", "50"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.min")).thenReturn(0.0);

            var commandData = commandInstance.parse(mockSender, args);

            assertNotNull(commandData);
            assertEquals(EcoCommand.CommandType.TAKE, commandData.commandType());
            assertEquals(50.0, commandData.amount());
        }
    }

    @Test
    void testParse_ValidArguments_TakeBelowMin() {
        var args = new String[] {"take", "player", "500000"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.min")).thenReturn(0.0);
            when(mockFeatherEconomy.getEconomy().format(0.0)).thenReturn("0.0");

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_TakeNegative() {
        var args = new String[] {"take", "player", "-50"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_Set() {
        var args = new String[] {"set", "player", "200"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.min")).thenReturn(0.0);

            var commandData = commandInstance.parse(mockSender, args);

            assertNotNull(commandData);
            assertEquals(EcoCommand.CommandType.SET, commandData.commandType());
            assertEquals(200.0, commandData.amount());
        }
    }

    @Test
    void testParse_ValidArguments_SetNegative() {
        var args = new String[] {"set", "player", "-200"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.min")).thenReturn(0.0);
            when(mockFeatherEconomy.getEconomy().format(0.0)).thenReturn("0.0");

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @Test
    void testParse_ValidArguments_SetOverMax() {
        var args = new String[] {"set", "player", "2000000"};
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getOfflinePlayer("player"))
                    .thenReturn(mockOfflinePlayer);
            when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

            when(mockFeatherEconomy.getEconomy().getBalance(mockOfflinePlayer)).thenReturn(100.0);
            when(mockFeatherEconomy.getConfig().getDouble("balance.max")).thenReturn(1000.0);
            when(mockFeatherEconomy.getEconomy().format(1000.0)).thenReturn("1000.0");

            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData);
            verify(mockSender).sendMessage(anyString());
        }
    }

    @ParameterizedTest
    @MethodSource("getOneArg")
    void testOnTabComplete_OneArg(String[] args, List<String> expected) {
        var completions = commandInstance.onTabComplete(args);

        assertEquals(expected, completions);
    }

    static Stream<Arguments> getOneArg() {
        return Stream.of(
                Arguments.of(new String[] {"g"}, List.of("give")),
                Arguments.of(new String[] {"t"}, List.of("take")),
                Arguments.of(new String[] {"s"}, List.of("set")),
                Arguments.of(new String[] {"invalid"}, List.of("set", "give", "take")),
                Arguments.of(new String[] {}, List.of("set", "give", "take")));
    }

    @ParameterizedTest
    @MethodSource("getTwoArgs")
    void testOnTabComplete_TwoArgs(
            String[] args, List<String> onlinePlayers, List<String> expected) {
        try (var mockedStringUtils = mockStatic(StringUtils.class)) {
            mockedStringUtils.when(StringUtils::getOnlinePlayers).thenReturn(onlinePlayers);
            mockedStringUtils.when(() -> StringUtils.filterStartingWith(any(), any()))
                    .thenCallRealMethod();

            var completions = commandInstance.onTabComplete(args);

            assertEquals(expected, completions);
        }
    }

    static Stream<Arguments> getTwoArgs() {
        return Stream.of(
                Arguments.of(
                        new String[] {"give", "p"},
                        List.of("player1", "player2"),
                        List.of("player1", "player2")),
                Arguments.of(
                        new String[] {"give", "player1"},
                        List.of("player11", "player22"),
                        List.of("player11")),
                Arguments.of(
                        new String[] {"give", "player"},
                        List.of("player11", "player22"),
                        List.of("player11", "player22")),
                Arguments.of(
                        new String[] {"give", ""},
                        List.of("player11", "player22"),
                        List.of("player11", "player22")),
                Arguments.of(
                        new String[] {"give", "player"},
                        List.of(),
                        List.of()));
    }

    @Test
    void testOnTabComplete_ThreeArgs() {
        var args = new String[] {"give", "player", ""};
        var completions = commandInstance.onTabComplete(args);

        assertEquals(List.of("amount"), completions);
    }
}
