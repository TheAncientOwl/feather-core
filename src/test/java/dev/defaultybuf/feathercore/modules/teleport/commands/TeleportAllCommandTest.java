/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAllCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit TeleportAllCommand#0.9
 * @description Unit tests for TeleportAllCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

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
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

class TeleportAllCommandTest extends FeatherCommandTest<TeleportAllCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock Player mockPlayer3;
    @Mock CommandSender mockCommandSender;

    @MockedModule ITeleport mockTeleport;

    @Override
    protected Class<TeleportAllCommand> getCommandClass() {
        return TeleportAllCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer2.getName()).thenReturn("player2");
        lenient().when(mockPlayer3.getName()).thenReturn("player3");
    }

    @Test
    void testHasPermission_True() {
        when(mockPlayer1.hasPermission("feathercore.teleport.all"))
                .thenReturn(true);

        assertTrue(commandInstance.hasPermission(mockPlayer1,
                new TeleportAllCommand.CommandData(mockPlayer2)));
        verifyNoInteractions(mockLanguage);
    }

    @Test
    void testHasPermission_False() {
        when(mockPlayer1.hasPermission("feathercore.teleport.all"))
                .thenReturn(false);

        assertFalse(commandInstance.hasPermission(mockPlayer1,
                new TeleportAllCommand.CommandData(mockPlayer1)));
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NO_PERMISSION));
    }

    @Test
    void testExecute_PlayerToSelf() {
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers)
                    .thenReturn(List.of(mockPlayer1, mockPlayer2, mockPlayer3));

            var data = new TeleportAllCommand.CommandData(mockPlayer1);

            commandInstance.execute(mockPlayer1, data);

            verify(mockTeleport).teleport(mockPlayer1, mockPlayer1);
            verify(mockTeleport).teleport(mockPlayer2, mockPlayer1);
            verify(mockTeleport).teleport(mockPlayer3, mockPlayer1);
            verify(mockLanguage).message(mockPlayer1, Message.Teleport.ALL_SELF);
        }
    }

    @Test
    void testExecute_PlayerToOther() {
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers)
                    .thenReturn(List.of(mockPlayer1, mockPlayer2, mockPlayer3));

            var data = new TeleportAllCommand.CommandData(mockPlayer2);

            commandInstance.execute(mockPlayer1, data);

            verify(mockTeleport).teleport(mockPlayer1, mockPlayer2);
            verify(mockTeleport).teleport(mockPlayer2, mockPlayer2);
            verify(mockTeleport).teleport(mockPlayer3, mockPlayer2);
            verify(mockLanguage).message(eq(mockPlayer1), eq(Message.Teleport.ALL_OTHER),
                    anyPlaceholder());
        }
    }

    @Test
    void testExecute_CommandSenderToOther() {
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers)
                    .thenReturn(List.of(mockPlayer1, mockPlayer2, mockPlayer3));

            var data = new TeleportAllCommand.CommandData(mockPlayer2);

            commandInstance.execute(mockCommandSender, data);

            verify(mockTeleport).teleport(mockPlayer1, mockPlayer2);
            verify(mockTeleport).teleport(mockPlayer2, mockPlayer2);
            verify(mockTeleport).teleport(mockPlayer3, mockPlayer2);
            verify(mockLanguage).message(eq(
                    mockCommandSender), eq(Message.Teleport.ALL_OTHER),
                    anyPlaceholder());
        }
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_TPALL);
    }

    @Test
    void testParse_Self_IssuerNotPlayer() {
        var args = new String[] {};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertEquals(mockPlayer1, result.where());
    }

    @Test
    void testParse_Self_IssuerPlayer() {
        var args = new String[] {};

        var result = commandInstance.parse(mockCommandSender, args);

        assertNull(result);
        verify(mockLanguage).message(mockCommandSender, Message.General.PLAYERS_ONLY);
    }

    @Test
    void testParse_Other_PlayerNotOnline() {
        var args = new String[] {"randomPlayer"};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayerExact("randomPlayer")).thenReturn(null);

            var result = commandInstance.parse(mockPlayer1, args);

            assertNull(result);
            verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_ONLINE_PLAYER),
                    anyPlaceholder());
        }
    }

    @Test
    void testParse_Other_PlayerOnline() {
        var args = new String[] {mockPlayer2.getName()};

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                    .thenReturn(mockPlayer2);

            var result = commandInstance.parse(mockPlayer1, args);

            assertNotNull(result);
            assertEquals(mockPlayer2, result.where());
        }
    }

    @ParameterizedTest
    @MethodSource("getTabCompleteTestCases")
    void testOnTabComplete(String[] args, List<String> onlinePlayers,
            List<String> expectedCompletions) {
        try (var mockedStringUtils = mockStatic(StringUtils.class)) {
            mockedStringUtils.when(StringUtils::getOnlinePlayers).thenReturn(onlinePlayers);
            mockedStringUtils.when(() -> StringUtils.filterStartingWith(anyList(), anyString()))
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
                        List.of("player1", "player2", "player11", "player22"),
                        List.of()),
                /* 3 */ Arguments.of(
                        new String[] {"arg1", "arg2"},
                        List.of("player1", "player2", "player11", "player22"),
                        List.of()),
                /* 4 */ Arguments.of(
                        new String[] {""},
                        List.of("player1", "player2", "player11", "player22"),
                        List.of("player1", "player2", "player11", "player22")),
                /* 5 */ Arguments.of(
                        new String[] {"p"},
                        List.of("player1", "player2", "player11", "player22"),
                        List.of("player1", "player2", "player11", "player22")),
                /* 6 */ Arguments.of(
                        new String[] {"player1"},
                        List.of("player1", "player2", "player11", "player22"),
                        List.of("player1", "player11")),
                /* 7 */ Arguments.of(
                        new String[] {"player3"},
                        List.of("player1", "player2", "player11", "player22"),
                        List.of()));
    }

}
