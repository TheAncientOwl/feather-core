/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit TeleportCommand#0.9
 * @description Unit tests for TeleportCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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

class TeleportCommandTest extends FeatherCommandTest<TeleportCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock Player mockPlayer3;
    @Mock CommandSender mockSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;

    @MockedModule(of = Module.Teleport) ITeleport mockTeleport;

    @Override
    protected Class<TeleportCommand> getCommandClass() {
        return TeleportCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer2.getName()).thenReturn("player2");
        lenient().when(mockPlayer3.getName()).thenReturn("player3");

    }

    @ParameterizedTest
    @CsvSource({
            /* 1 */ "true, true , true , true ",
            /* 2 */ "true, true , false, true ",
            /* 3 */ "true, false, true , false",
            /* 4 */ "true, false, false, false",

            /* 5 */ "false, true , true , true ",
            /* 6 */ "false, true , false, false",
            /* 7 */ "false, false, true , false",
            /* 8 */ "false, false, false, false"})
    void testHasPermission(boolean isSelfTest, boolean hasSelf, boolean hasOther,
            boolean expectHasPermission) {
        lenient().when(mockPlayer1.hasPermission("feathercore.teleport.player.self"))
                .thenReturn(hasSelf);
        lenient().when(mockPlayer1.hasPermission("feathercore.teleport.player.other"))
                .thenReturn(hasOther);

        assertEquals(expectHasPermission, commandInstance.hasPermission(mockPlayer1,
                new TeleportCommand.CommandData(mockPlayer1,
                        isSelfTest ? mockPlayer1 : mockPlayer2,
                        isSelfTest)));

        if (expectHasPermission) {
            verifyNoInteractions(mockLanguage);
        } else {
            verify(mockLanguage).message(mockPlayer1, Message.General.NO_PERMISSION);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testExecute(boolean isSelfTest) {
        var data = new TeleportCommand.CommandData(mockPlayer1,
                isSelfTest ? mockPlayer1 : mockPlayer2, isSelfTest);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer1, data);
        });

        verify(mockTeleport).teleport(data.who(), data.destination());
        verify(mockLanguage).message(eq(mockPlayer1),
                eq(isSelfTest ? Message.Teleport.PLAYER_SELF
                        : Message.Teleport.PLAYER),
                anyList());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2", "arg3"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_PLAYER);
    }

    @Test
    void testParse_SenderToPlayer2() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockLanguage).message(mockSender, Message.General.PLAYERS_ONLY);
    }

    @Test
    void testParse_Player1ToOnlinePlayer2_IsSelf() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertTrue(result.selfTeleport());
        assertEquals(mockPlayer1, result.who());
        assertEquals(mockPlayer2, result.destination());
    }

    @Test
    void testParse_Player1Teleport_Player2ToPlayer3_IsNotSelf() {
        var args = new String[] {mockPlayer2.getName(), mockPlayer3.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer3.getName()))
                .thenReturn(mockPlayer3);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertFalse(result.selfTeleport());
        assertEquals(mockPlayer2, result.who());
        assertEquals(mockPlayer3, result.destination());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParse_Player1Teleport_Player2ToPlayer3NotOnline_IsNotSelf() {
        var args = new String[] {mockPlayer2.getName(), mockPlayer3.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer3.getName()))
                .thenReturn(null);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_ONLINE_PLAYER),
                anyPair());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParse_Player1ToNotOnlinePlayer2() {
        var args = new String[] {mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(null);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_ONLINE_PLAYER),
                anyPair());
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
                        List.of()),

                /* 8 */ Arguments.of(new String[] {"arg",}, List.of(), List.of()),
                /* 9 */ Arguments.of(
                        new String[] {"arg",},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 10 */ Arguments.of(
                        new String[] {"arg", "arg1", "arg2"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 11 */ Arguments.of(
                        new String[] {"arg", ""},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 12 */ Arguments.of(
                        new String[] {"arg", "p"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 13 */ Arguments.of(
                        new String[] {"arg", "player1"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player11")),
                /* 14 */ Arguments.of(
                        new String[] {"arg", "player3"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()));
    }

}
