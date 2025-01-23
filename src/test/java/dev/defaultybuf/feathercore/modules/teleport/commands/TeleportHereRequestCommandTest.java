/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportHereRequestCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit TeleportHereRequestCommand#0.7
 * @description Unit tests for TeleportHereRequestCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.StaticMock;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestStatus;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestType;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class TeleportHereRequestCommandTest extends FeatherCommandTest<TeleportHereRequestCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock CommandSender mockCommandSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;

    @MockedModule ITeleport mockTeleport;

    @Override
    protected Class<TeleportHereRequestCommand> getCommandClass() {
        return TeleportHereRequestCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer2.getName()).thenReturn("player2");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testHasPermission(boolean has) {
        when(mockPlayer1.hasPermission("feathercore.teleport.request.here"))
                .thenReturn(has);

        assertEquals(has, commandInstance.hasPermission(mockPlayer1,
                new TeleportHereRequestCommand.CommandData(mockPlayer1,
                        mockPlayer2)));

        verify(mockLanguage, has ? never() : times(1)).message(mockPlayer1,
                Message.General.NO_PERMISSION);
    }

    @Test
    void testExecute_AlreadyRequested() {
        var data = new TeleportHereRequestCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.request(data.issuer(), data.target(), RequestType.HERE))
                .thenReturn(RequestStatus.ALREADY_REQUESTED);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage).message(eq(mockPlayer1),
                eq(Message.Teleport.REQUEST_HERE_EXECUTE_PENDING),
                anyPlaceholder());
    }

    @Test
    void testExecute_Requested() {
        var data = new TeleportHereRequestCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.request(data.issuer(), data.target(), RequestType.HERE))
                .thenReturn(RequestStatus.REQUESTED);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage).message(eq(data.issuer()),
                eq(Message.Teleport.REQUEST_HERE_EXECUTE_ISSUER), anyPlaceholder());
        verify(mockLanguage).message(eq(data.target()),
                eq(Message.Teleport.REQUEST_HERE_EXECUTE_TARGET), anyPlaceholder());
    }

    @ParameterizedTest
    @MethodSource("getLogicErrorRequestStatuses")
    void testExecute_LogicErrorRequestType(Teleport.RequestStatus requestStatus) {
        clearInvocations(mockLanguage, mockPlayer1, mockPlayer2);

        var data = new TeleportHereRequestCommand.CommandData(mockPlayer1, mockPlayer2);

        when(mockTeleport.request(data.issuer(), data.target(), RequestType.HERE))
                .thenReturn(requestStatus);

        assertThrows(AssertionError.class, () -> {
            commandInstance.execute(mockPlayer1, data);
        });

        verifyNoInteractions(mockLanguage, mockPlayer1, mockPlayer2);
    }

    static Stream<Arguments> getLogicErrorRequestStatuses() {
        return Stream.of(
                /* 1 */ Arguments.of(Teleport.RequestStatus.NO_SUCH_REQUEST),
                /* 2 */ Arguments.of(Teleport.RequestStatus.CANCELLED),
                /* 3 */ Arguments.of(Teleport.RequestStatus.ACCEPTED));
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_REQUEST_HERE);
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
        assertEquals(mockPlayer1, result.issuer());
        assertEquals(mockPlayer2, result.target());
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
