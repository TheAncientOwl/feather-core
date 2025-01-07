/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAcceptCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit TeleportAcceptCommand#0.8
 * @description Unit tests for TeleportAcceptCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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
import org.mockito.MockedStatic;

import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.StaticMock;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherCommandTest;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestStatus;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

@SuppressWarnings("unchecked")
class TeleportAcceptCommandTest extends FeatherCommandTest<TeleportAcceptCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock CommandSender mockCommandSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;

    @MockedModule(of = Module.Teleport) ITeleport mockTeleport;

    @Override
    protected Class<TeleportAcceptCommand> getCommandClass() {
        return TeleportAcceptCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer2.getName()).thenReturn("player2");

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer1.getName()))
                .thenReturn(mockPlayer1);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
    }

    @Test
    void testHasPermission_True() {
        when(mockPlayer1.hasPermission("feathercore.teleport.request.accept"))
                .thenReturn(true);

        assertTrue(commandInstance.hasPermission(mockPlayer1,
                new TeleportAcceptCommand.CommandData(mockPlayer2, mockPlayer1)));
        verifyNoInteractions(mockLanguage);
    }

    @Test
    void testHasPermission_False() {
        when(mockPlayer1.hasPermission("feathercore.teleport.request.accept"))
                .thenReturn(false);

        assertFalse(commandInstance.hasPermission(mockPlayer1,
                new TeleportAcceptCommand.CommandData(mockPlayer1, mockPlayer2)));
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NO_PERMISSION));
    }

    @Test
    void testExecute_NoSuchRequest() {
        var data = new TeleportAcceptCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.acceptRequest(data.issuer(), data.target()))
                .thenReturn(RequestStatus.NO_SUCH_REQUEST);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage).message(mockPlayer1, Message.Teleport.NO_SUCH_REQUEST);
        verify(mockLanguage, never()).message(
                eq(data.issuer()),
                eq(Message.Teleport.REQUEST_ACCEPT_ISSUER),
                anyPair());
        verify(mockLanguage, never()).message(
                eq(data.target()),
                eq(Message.Teleport.REQUEST_ACCEPT_TARGET),
                anyPair());
    }

    @Test
    void testExecute_RequestAccepted_NoDelay() {
        var data = new TeleportAcceptCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.acceptRequest(data.issuer(), data.target()))
                .thenReturn(RequestStatus.ACCEPTED);

        when(mockTeleport.getConfig().getMillis("request.accept-delay")).thenReturn(0L);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage).message(
                eq(data.issuer()),
                eq(Message.Teleport.REQUEST_ACCEPT_ISSUER),
                anyPair());
        verify(mockLanguage).message(
                eq(data.target()),
                eq(Message.Teleport.REQUEST_ACCEPT_TARGET),
                anyPair());
        verify(mockLanguage, never()).message(
                eq(data.target()),
                eq(Message.Teleport.REQUEST_DELAY),
                anyPair());
    }

    @ParameterizedTest
    @MethodSource("getLogicErrorRequestStatuses")
    void testExecute_LogicErrorRequestType(Teleport.RequestStatus requestStatus) {
        clearInvocations(mockLanguage, mockPlayer1, mockPlayer2);

        var data = new TeleportAcceptCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.acceptRequest(data.issuer(), data.target()))
                .thenReturn(requestStatus);

        assertThrows(AssertionError.class, () -> {
            commandInstance.execute(mockPlayer1, data);
        });

        verifyNoInteractions(mockLanguage, mockPlayer1, mockPlayer2);
    }

    static Stream<Arguments> getLogicErrorRequestStatuses() {
        return Stream.of(
                /* 1 */ Arguments.of(Teleport.RequestStatus.ALREADY_REQUESTED),
                /* 2 */ Arguments.of(Teleport.RequestStatus.REQUESTED),
                /* 3 */ Arguments.of(Teleport.RequestStatus.CANCELLED));
    }

    @Test
    void testExecute_RequestAccepted_Delay() {
        var data = new TeleportAcceptCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.acceptRequest(data.issuer(), data.target()))
                .thenReturn(RequestStatus.ACCEPTED);

        when(mockTeleport.getConfig().getMillis("request.accept-delay")).thenReturn(1000L);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage).message(
                eq(data.issuer()),
                eq(Message.Teleport.REQUEST_ACCEPT_ISSUER),
                anyPair());
        verify(mockLanguage).message(
                eq(data.target()),
                eq(Message.Teleport.REQUEST_ACCEPT_TARGET),
                anyPair());
        verify(mockLanguage).message(
                eq(data.target()),
                eq(Message.Teleport.REQUEST_DELAY),
                anyPair());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.USAGE_INVALID),
                eq(Message.Teleport.USAGE_REQUEST_ACCEPT));
    }

    @Test
    void testParse_AcceptPlayer_Online() {
        var args = new String[] {mockPlayer2.getName()};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);

        assertEquals(mockPlayer2, result.issuer());
        assertEquals(mockPlayer1, result.target());
    }

    @Test
    void testParse_AcceptPlayer_CommandSenderNotPlayer() {
        var args = new String[] {mockPlayer2.getName()};

        var result = commandInstance.parse(mockCommandSender, args);

        assertNull(result);
        verify(mockLanguage).message(mockCommandSender, Message.General.PLAYERS_ONLY);
    }

    @Test
    void testParse_AcceptPlayer_NotOnline() {
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(null);

        var args = new String[] {mockPlayer2.getName()};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
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
                        List.of()));
    }

}
