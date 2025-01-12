/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportHereCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit TeleportHereCommand#0.9
 * @description Unit tests for TeleportHereCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.StaticMock;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherCommandTest;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

class TeleportHereCommandTest extends FeatherCommandTest<TeleportHereCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock CommandSender mockCommandSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;

    @MockedModule(of = Module.Teleport) ITeleport mockTeleport;

    @Override
    protected Class<TeleportHereCommand> getCommandClass() {
        return TeleportHereCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer2.getName()).thenReturn("player2");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testHasPermission(boolean has) {
        when(mockPlayer1.hasPermission("feathercore.teleport.here"))
                .thenReturn(has);

        assertEquals(has, commandInstance.hasPermission(mockPlayer1,
                new TeleportHereCommand.CommandData(mockPlayer2)));

        verify(mockLanguage, has ? never() : times(1)).message(mockPlayer1,
                Message.General.NO_PERMISSION);
    }

    @Test
    void testExecute() {
        var data = new TeleportHereCommand.CommandData(mockPlayer2);

        commandInstance.execute(mockPlayer1, data);

        verify(mockTeleport).teleport(data.who(), mockPlayer1);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.Teleport.HERE),
                anyPlaceholder());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_TPHERE);
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);

        var result = commandInstance.parse(mockCommandSender, args);

        assertNull(result);
        verify(mockLanguage).message(mockCommandSender, Message.General.PLAYERS_ONLY);
    }

    @Test
    void testParse_Player1_NotOnlinePlayer2() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(null);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_ONLINE_PLAYER),
                anyPlaceholder());
    }

    @Test
    void testParse_Player1_OnlinePlayer2() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertEquals(mockPlayer2, result.who());
    }

    @ParameterizedTest
    @MethodSource("getTabCompleteTestCases")
    void testOnTabComplete(String[] args, List<String> onlinePlayers,
            List<String> expectedCompletions) {
        try (var mockedStringUtils = mockStatic(StringUtils.class)) {
            mockedStringUtils.when(StringUtils::getOnlinePlayers)
                    .thenReturn(onlinePlayers);
            mockedStringUtils
                    .when(() -> StringUtils.filterStartingWith(anyList(),
                            anyString()))
                    .thenCallRealMethod();

            var result = commandInstance.onTabComplete(args);

            assertEquals(expectedCompletions, result);
        }
    }

    static Stream<Arguments> getTabCompleteTestCases() {
        return Stream.of(
                /* 1 */ Arguments.of(new String[] {}, List.of(), List.of()),
                /* 2 */ Arguments.of(
                        new String[] {},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 3 */ Arguments.of(
                        new String[] {"arg1", "arg2"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 4 */ Arguments.of(
                        new String[] {""},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 5 */ Arguments.of(
                        new String[] {"p"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 6 */ Arguments.of(
                        new String[] {"player1"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player11")),
                /* 7 */ Arguments.of(
                        new String[] {"player3"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()));
    }

}
