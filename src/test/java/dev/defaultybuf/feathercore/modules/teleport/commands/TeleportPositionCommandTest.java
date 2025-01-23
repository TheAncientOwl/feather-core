/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportPositionCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @test_unit TeleportPositionCommand#0.10
 * @description Unit tests for TeleportPositionCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.StaticMock;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
public class TeleportPositionCommandTest extends FeatherCommandTest<TeleportPositionCommand> {
    @Mock World mockWorld;
    @Mock Player mockPlayer1;
    @Mock Player mockPlayer2;
    @Mock CommandSender mockSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;
    @StaticMock(of = StringUtils.class) MockedStatic<StringUtils> mockedStringUtils;

    @MockedModule ITeleport mockTeleport;

    @Override
    protected Class<TeleportPositionCommand> getCommandClass() {
        return TeleportPositionCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockWorld.getName()).thenReturn("world");

        lenient().when(mockPlayer1.getName()).thenReturn("player1");
        lenient().when(mockPlayer1.getWorld()).thenReturn(mockWorld);

        lenient().when(mockPlayer2.getName()).thenReturn("player2");
        lenient().when(mockPlayer2.getWorld()).thenReturn(mockWorld);

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer1.getName()))
                .thenReturn(mockPlayer1);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName()))
                .thenReturn(mockPlayer2);
    }

    @ParameterizedTest
    @CsvSource({
            /* 1 */ "true  , true  , true  , true  ",
            /* 2 */ "true  , false , true  , false ",
            /* 3 */ "true  , true  , false , true  ",
            /* 4 */ "true  , false , false , false ",

            /* 5 */" false , true  , true  , true  ",
            /* 6 */" false , false , true  , false ",
            /* 7 */" false , true  , false , false ",
            /* 8 */" false , false , false , false "})
    void testHasPermission(boolean isSelf, boolean hasSelf, boolean hasOther,
            boolean expectHasPermission) {
        var issuer = isSelf ? mockPlayer1 : mockSender;
        var target = isSelf ? mockPlayer1 : mockPlayer2;

        lenient().when(issuer.hasPermission("feathercore.teleport.world.position"))
                .thenReturn(hasSelf);
        lenient().when(issuer.hasPermission("feathercore.teleport.world.position.other"))
                .thenReturn(hasOther);

        var data = new TeleportPositionCommand.CommandData(target, 0, 0,
                64, mockWorld);

        assertEquals(expectHasPermission,
                commandInstance.hasPermission(issuer, data));
        verify(mockLanguage, expectHasPermission ? never() : times(1)).message(issuer,
                Message.General.NO_PERMISSION);
    }

    @Test
    void testExecute_TeleportSelf() {
        var data = new TeleportPositionCommand.CommandData(mockPlayer1, 1, 1, 1, mockWorld);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer1, data);
        });

        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.Teleport.POSITION), anyList());
    }

    @Test
    void testExecute_TeleportOther() {
        var data = new TeleportPositionCommand.CommandData(mockPlayer2, 1, 1, 1, mockWorld);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer1, data);
        });

        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.Teleport.POSITION_OTHER),
                anyList());
        verify(mockLanguage).message(eq(mockPlayer2), eq(Message.Teleport.POSITION), anyList());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"arg1", "arg2", "arg3", "arg4", "arg5", "arg6"};

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(mockPlayer1, Message.General.USAGE_INVALID,
                Message.Teleport.USAGE_POSITION);
    }

    @Test
    void testParse_XYZ_Numbers() {
        var args = new String[] {"123", "64", "505"};

        var data = commandInstance.parse(mockPlayer1, args);

        assertNotNull(data);
        assertEquals(123, data.x());
        assertEquals(64, data.y());
        assertEquals(505, data.z());
        assertEquals(mockPlayer1, data.player());
        assertEquals(mockWorld, data.world());
    }

    @Test
    void testParse_XYZ_YNotNumber() {
        var args = new String[] {"123", "nan", "505"};

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NAN), anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_World() {
        var args = new String[] {"123", "64", "505", mockWorld.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNotNull(data);
        assertEquals(123, data.x());
        assertEquals(64, data.y());
        assertEquals(505, data.z());
        assertEquals(mockPlayer1, data.player());
        assertEquals(mockWorld, data.world());
    }

    @Test
    void testParse_XYZ_Numbers_YNotNumber_World() {
        var args = new String[] {"123", "nan", "505", mockWorld.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NAN), anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_WorldInvalid() {
        var args = new String[] {"123", "64", "505", mockWorld.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(null);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_VALUE),
                anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_Player() {
        var args = new String[] {"123", "64", "505", mockPlayer2.getName()};

        var data = commandInstance.parse(mockPlayer1, args);

        assertNotNull(data);
        assertEquals(123, data.x());
        assertEquals(64, data.y());
        assertEquals(505, data.z());
        assertEquals(mockPlayer2, data.player());
        assertEquals(mockWorld, data.world());
    }

    @Test
    void testParse_XYZ_Numbers_PlayerInvalid() {
        var args = new String[] {"123", "64", "505", mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName())).thenReturn(null);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_VALUE),
                anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_World_Player() {
        var args = new String[] {"123", "64", "505", mockWorld.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNotNull(data);
        assertEquals(123, data.x());
        assertEquals(64, data.y());
        assertEquals(505, data.z());
        assertEquals(mockPlayer2, data.player());
        assertEquals(mockWorld, data.world());
    }

    @Test
    void testParse_XYZ_Numbers_YNotNumber_World_Player() {
        var args = new String[] {"123", "nan", "505", mockWorld.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NAN), anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_WorldInvalid_Player() {
        var args = new String[] {"123", "64", "505", mockWorld.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(null);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_WORLD),
                anyPlaceholder());
    }

    @Test
    void testParse_XYZ_Numbers_World_PlayerInvalid() {
        var args = new String[] {"123", "64", "505", mockWorld.getName(), mockPlayer2.getName()};

        mockedBukkit.when(() -> Bukkit.getWorld(mockWorld.getName())).thenReturn(mockWorld);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockPlayer2.getName())).thenReturn(null);

        var data = commandInstance.parse(mockPlayer1, args);

        assertNull(data);
        verify(mockLanguage).message(eq(mockPlayer1), eq(Message.General.NOT_VALID_PLAYER),
                anyPlaceholder());
    }

    @ParameterizedTest
    @MethodSource("getTabCompleteTestCases")
    void testOnTabComplete(String[] args, List<String> onlinePlayers, List<String> worlds,
            List<String> expectedCompletions) {
        mockedStringUtils.when(StringUtils::getOnlinePlayers)
                .thenReturn(onlinePlayers);
        mockedStringUtils.when(StringUtils::getWorlds).thenReturn(worlds);
        mockedStringUtils
                .when(() -> StringUtils.filterStartingWith(anyList(),
                        anyString()))
                .thenCallRealMethod();

        var result = commandInstance.onTabComplete(args);

        assertEquals(expectedCompletions, result);
    }

    // @formatter:off
    static Stream<Arguments> getTabCompleteTestCases() {
        return Stream.of(
            /* 1 */ Arguments.of(
                    new String[] {},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of()),
            /* 2 */ Arguments.of(
                    new String[] {""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("x")),
            /* 3 */ Arguments.of(
                    new String[] {"", ""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("y")),
            /* 4 */ Arguments.of(
                    new String[] {"", "", ""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("z")),
            /* 5 */ Arguments.of(
                    new String[] {"", "", "", ""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("world1", "world2", "world11", "world22")),
            /* 6 */ Arguments.of(
                    new String[] {"", "", "", "w"}, 
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("world1", "world2", "world11", "world22")),
            /* 7 */ Arguments.of(
                    new String[] {"", "", "", "world1"}, 
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("world1", "world11")),
            /* 8 */ Arguments.of(
                    new String[] {"", "", "", "", ""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of("player1", "player2", "player11", "player22")),
            /* 9 */ Arguments.of(
                    new String[] {"", "", "", "", "", ""},
                    List.of("player1", "player2", "player11", "player22"),
                    List.of("world1", "world2", "world11", "world22"),
                    List.of())
        );
    }
    // @formatter:on

}
