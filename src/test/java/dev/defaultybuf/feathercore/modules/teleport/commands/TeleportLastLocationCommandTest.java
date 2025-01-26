/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportLastLocationCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.11
 * @test_unit TeleportLastLocationCommand#0.10
 * @description Unit tests for TeleportLastLocationCommand
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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

import dev.defaultybuf.feather.toolkit.testing.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.StaticMock;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feathercore.common.FeatherCoreDependencyFactory;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LocationModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class TeleportLastLocationCommandTest extends FeatherCommandTest<TeleportLastLocationCommand> {
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock World mockWorld;
    @Mock Location mockLocation;
    @Mock OfflinePlayer mockOfflinePlayer;
    @Mock CommandSender mockCommandSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;

    @MockedModule ITeleport mockTeleport;
    @MockedModule IPlayersData mockPlayersData;

    PlayerModel playerModel;

    @Override
    protected Class<TeleportLastLocationCommand> getCommandClass() {
        return TeleportLastLocationCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer1.getName()).thenReturn("player2");
        lenient().when(mockOfflinePlayer.getName()).thenReturn("playerOffline");

        lenient().when(mockWorld.getName()).thenReturn("world");
        lenient().when(mockLocation.getWorld()).thenReturn(mockWorld);
        lenient().when(mockLocation.getX()).thenReturn(2D);
        lenient().when(mockLocation.getY()).thenReturn(64D);
        lenient().when(mockLocation.getZ()).thenReturn(2D);

        playerModel = new PlayerModel();
        playerModel.lastKnownLocation = new LocationModel(mockLocation);
        lenient().when(mockPlayersData.getPlayerModel(mockOfflinePlayer))
                .thenReturn(playerModel);
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
        lenient().when(mockPlayer1.hasPermission("feathercore.teleport.lastknown"))
                .thenReturn(hasSelf);
        lenient().when(mockPlayer1.hasPermission("feathercore.teleport.lastknown.other"))
                .thenReturn(hasOther);

        assertEquals(expectHasPermission, commandInstance.hasPermission(mockPlayer1,
                new TeleportLastLocationCommand.CommandData(mockPlayer1,
                        playerModel.lastKnownLocation, isSelfTest)));

        if (expectHasPermission) {
            verifyNoInteractions(mockLanguage);
        } else {
            verify(mockLanguage).message(mockPlayer1, Message.General.NO_PERMISSION);
        }
    }

    @Test
    void testExecute_WorldNoLongerAvailable() {
        var data = new TeleportLastLocationCommand.CommandData(mockPlayer1,
                playerModel.lastKnownLocation, true);

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(null);

        commandInstance.execute(mockPlayer1, data);

        verifyNoInteractions(mockTeleport);
        verify(mockLanguage).message(eq(mockPlayer1),
                eq(Message.General.WORLD_NO_LONGER_AVAILABLE),
                anyPlaceholder());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testExecute_Teleport(boolean isSelf) {
        var data = new TeleportLastLocationCommand.CommandData(mockPlayer1,
                playerModel.lastKnownLocation, isSelf);

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);

        commandInstance.execute(mockPlayer1, data);

        verify(mockLanguage, never()).message(eq(mockPlayer1),
                eq(Message.General.WORLD_NO_LONGER_AVAILABLE),
                anyPlaceholder());

        verify(mockTeleport).teleport(mockPlayer1,
                data.destination().x, data.destination().y, data.destination().z,
                mockWorld);

        verify(mockLanguage).message(eq(data.who()), eq(Message.Teleport.POSITION),
                anyList());

        if (!isSelf) {
            verify(mockLanguage).message(eq(mockPlayer1),
                    eq(Message.Teleport.POSITION_OTHER),
                    anyList());
        }
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2", "arg3"};

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_OFFLINE);
    }

    @Test
    void testParse_Self_SenderNotPlayer() {
        var args = new String[] {mockOfflinePlayer.getName()};

        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        var result = commandInstance.parse(mockCommandSender, args);

        assertNull(result);
        verify(mockLanguage).message(mockCommandSender, Message.General.PLAYERS_ONLY);
    }

    @Test
    void testParse_Self_InvalidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName()};

        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(false);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_PLAYER),
                anyPlaceholder());
    }

    @Test
    void testParse_Self_ValidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName()};

        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertTrue(result.selfTeleport());
        assertEquals(mockPlayer1, result.who());
        assertEquals(playerModel.lastKnownLocation, result.destination());
    }

    @Test
    void testParse_Self_ValidPlayer_MissingDestination() {
        var args = new String[] {mockOfflinePlayer.getName()};

        when(mockPlayersData.getPlayerModel(mockOfflinePlayer)).thenReturn(null);

        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_PLAYER),
                anyPlaceholder());
    }

    @Test
    void testParse_OtherOnline_InvalidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(false);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_PLAYER),
                anyPlaceholder());
    }

    @Test
    void testParse_OtherOnline_ValidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNotNull(result);
        assertFalse(result.selfTeleport());
        assertEquals(mockPlayer2, result.who());
        assertEquals(playerModel.lastKnownLocation, result.destination());
    }

    @Test
    void testParse_OtherOffline_InvalidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(null);
        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(false);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_PLAYER),
                anyPlaceholder());
    }

    @Test
    void testParse_OtherOffline_ValidPlayer() {
        var args = new String[] {mockOfflinePlayer.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(null);
        mockedBukkit.when(() -> Bukkit.getOfflinePlayer(mockOfflinePlayer.getName()))
                .thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        var result = commandInstance.parse(mockPlayer1, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_ONLINE_PLAYER),
                anyPlaceholder());
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
                        new String[] {""},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player")),
                /* 3 */ Arguments.of(
                        new String[] {"argPlayer", "arg1", "arg2"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 4 */ Arguments.of(
                        new String[] {"argPlayer", ""},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 5 */ Arguments.of(
                        new String[] {"argPlayer", "p"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 6 */ Arguments.of(
                        new String[] {"argPlayer", "player1"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player11")),
                /* 7 */ Arguments.of(
                        new String[] {"argPlayer", "player3"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 8 */ Arguments.of(new String[] {""}, List.of(),
                        List.of("player")));
    }

}
