/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file RandomTeleportCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @test_unit RandomTeleportCommand#0.11
 * @description Unit tests for RandomTeleportCommand
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.WorldUtils;
import dev.defaultybuf.feathercore.api.common.util.Clock;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.configuration.IConfigSection;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.StaticMock;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherCommandTest;
import dev.defaultybuf.feathercore.modules.common.utils.Argumentable;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

class RandomTeleportCommandTest extends FeatherCommandTest<RandomTeleportCommand> {
    static final long INITIAL_TIME = 0L;

    @Mock World mockWorld;
    @Mock WorldBorder mockWorldBorder;
    @Mock IConfigSection mockWorldConfig;

    @Mock Player mockIssuerPlayer;
    @Mock Player mockTargetPlayer;
    @Mock Location mockTargetLocation;
    @Mock Location mockRandomLocation;

    @Mock CommandSender mockSender;

    @StaticMock(of = Bukkit.class) MockedStatic<Bukkit> mockedBukkit;
    @StaticMock(of = Clock.class) MockedStatic<Clock> mockedClock;
    @StaticMock(of = WorldUtils.class) MockedStatic<WorldUtils> mockedWorldUtils;

    @MockedModule(of = Module.Teleport) ITeleport mockTeleport;

    UUID mockIssuerUUID;
    UUID mockTargetUUID;

    @Override
    protected Class<RandomTeleportCommand> getCommandClass() {
        return RandomTeleportCommand.class;
    }

    @Override
    protected void setUp() {
        mockIssuerUUID = UUID.randomUUID();
        mockTargetUUID = UUID.randomUUID();

        lenient().when(mockWorld.getName()).thenReturn("TEST_WORLD");
        lenient().when(mockWorld.getWorldBorder()).thenReturn(mockWorldBorder);

        lenient().when(mockIssuerPlayer.getName()).thenReturn("issuer");
        lenient().when(mockIssuerPlayer.getWorld()).thenReturn(mockWorld);
        lenient().when(mockIssuerPlayer.getUniqueId()).thenReturn(mockIssuerUUID);

        lenient().when(mockTargetPlayer.getName()).thenReturn("target");
        lenient().when(mockTargetPlayer.getWorld()).thenReturn(mockWorld);
        lenient().when(mockTargetPlayer.getUniqueId()).thenReturn(mockTargetUUID);
        lenient().when(mockTargetPlayer.getLocation()).thenReturn(mockTargetLocation);

        lenient().when(mockTargetLocation.getWorld()).thenReturn(mockWorld);

        lenient().when(mockTeleport.getConfig().getMillis("random.cooldown"))
                .thenReturn(2500L);
        lenient().when(mockTeleport.getConfig()
                .getConfigurationSection("random.TEST_WORLD"))
                .thenReturn(mockWorldConfig);

        lenient().when(mockWorldConfig.getInt("trials")).thenReturn(3);
        lenient().when(mockWorldConfig.getInt("min-distance")).thenReturn(500);
        lenient().when(mockWorldConfig.getInt("max-distance")).thenReturn(2500);
        lenient().when(mockWorldConfig.getInt("altitude.min")).thenReturn(100);
        lenient().when(mockWorldConfig.getInt("altitude.max")).thenReturn(60);

        lenient().when(mockRandomLocation.getWorld()).thenReturn(mockWorld);

        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockIssuerPlayer.getName()))
                .thenReturn(mockIssuerPlayer);
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockTargetPlayer.getName()))
                .thenReturn(mockTargetPlayer);
    }

    public static class HasPermissionTestCase extends Argumentable {
        public CommandSender sender;
        public Player target;

        public boolean senderCanSelf;
        public boolean senderCanOther;

        public boolean expectHasPermission;
        public boolean expectMessage;

        // @formatter:off
        public HasPermissionTestCase sender(CommandSender value) { this.sender = value; return this; }
        public HasPermissionTestCase target(Player value) { this.target = value; return this; }
        public HasPermissionTestCase senderCanSelf(boolean value) { this.senderCanSelf = value; return this; }
        public HasPermissionTestCase senderCanOther(boolean value) { this.senderCanOther = value; return this; }
        public HasPermissionTestCase expectHasPermission(boolean value) { this.expectHasPermission = value; return this; }
        public HasPermissionTestCase expectMessage(boolean value) { this.expectMessage = value; return this; }
        // @formatter:on
    }

    @ParameterizedTest
    @CsvSource({
            /* 1 */ " true  , true  , true  ",
            /* 2 */ " true  , false , true  ",
            /* 3 */ " false , true  , false ",
            /* 4 */ " false , false , false "})
    void testHasPermission_PlayerSender_Self(
            boolean senderCanSelf, boolean senderCanOther,
            boolean expectHasPermission) {
        var sender = mockIssuerPlayer;
        var target = mockIssuerPlayer;

        lenient().when(sender.hasPermission("feathercore.teleport.random.self.TEST_WORLD"))
                .thenReturn(senderCanSelf);
        lenient().when(sender.hasPermission("feathercore.teleport.random.other.TEST_WORLD"))
                .thenReturn(senderCanOther);

        assertEquals(expectHasPermission, commandInstance.hasPermission(sender,
                new RandomTeleportCommand.CommandData(target)));

        if (!expectHasPermission) {
            verify(mockLanguage).message(eq(sender), eq(Message.General.NO_PERMISSION));
        }
    }

    @ParameterizedTest
    @CsvSource({
            /* 1 */ " true  , true  , true  ",
            /* 2 */ " true  , false , false ",
            /* 3 */ " false , true  , false ",
            /* 4 */ " false , false , false "})
    void testHasPermission_PlayerSender_Other(boolean senderCanSelf, boolean senderCanOther,
            boolean expectHasPermission) {
        var sender = mockIssuerPlayer;
        var target = mockTargetPlayer;

        assertTrue(sender.equals(sender));
        assertTrue(target.equals(target));

        lenient().when(sender.hasPermission("feathercore.teleport.random.self.TEST_WORLD"))
                .thenReturn(senderCanSelf);
        lenient().when(sender.hasPermission("feathercore.teleport.random.other.TEST_WORLD"))
                .thenReturn(senderCanOther);

        assertEquals(expectHasPermission, commandInstance.hasPermission(sender,
                new RandomTeleportCommand.CommandData(target)));

        if (!expectHasPermission) {
            verify(mockLanguage).message(eq(sender), eq(Message.General.NO_PERMISSION));
        }
    }

    @ParameterizedTest
    @CsvSource({
            /* 1 */ " true  , true  , true  ",
            /* 2 */ " true  , false , false ",
            /* 3 */ " false , true  , false ",
            /* 4 */ " false , false , false "})
    void testHasPermission_NonPlayerSender_Other(boolean senderCanSelf, boolean senderCanOther,
            boolean expectHasPermission) {
        var sender = mockSender;
        var target = mockTargetPlayer;

        assertTrue(sender.equals(sender));
        assertTrue(target.equals(target));

        lenient().when(sender.hasPermission("feathercore.teleport.random.self.TEST_WORLD"))
                .thenReturn(senderCanSelf);
        lenient().when(sender.hasPermission("feathercore.teleport.random.other.TEST_WORLD"))
                .thenReturn(senderCanOther);

        assertEquals(expectHasPermission, commandInstance.hasPermission(sender,
                new RandomTeleportCommand.CommandData(target)));

        if (!expectHasPermission) {
            verify(mockLanguage).message(eq(sender), eq(Message.General.NO_PERMISSION));
        }
    }

    @ParameterizedTest
    @MethodSource("getOnTabCompleteData")
    void testOnTabComplete(String[] args, List<String> onlinePlayers,
            List<String> expectedCompletions) {
        try (var mockedStringUtils = mockStatic(StringUtils.class)) {
            mockedStringUtils.when(StringUtils::getOnlinePlayers)
                    .thenReturn(onlinePlayers);
            mockedStringUtils
                    .when(() -> StringUtils.filterStartingWith(anyList(),
                            anyString()))
                    .thenCallRealMethod();

            assertEquals(expectedCompletions, commandInstance.onTabComplete(args));
        }
    }

    static Stream<Arguments> getOnTabCompleteData() {
        return Stream.of(
                /* 1 */ Arguments.of(new String[] {}, List.of(), List.of()),
                /* 2 */ Arguments.of(new String[] {""}, List.of(), List.of()),
                /* 3 */ Arguments.of(new String[] {"p"}, List.of(), List.of()),
                /* 4 */ Arguments.of(new String[] {"player"}, List.of(), List.of()),
                /* 5 */ Arguments.of(new String[] {}, List.of("player1", "player2"),
                        List.of()),
                /* 6 */ Arguments.of(
                        new String[] {""},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 7 */ Arguments.of(
                        new String[] {"arg1", "arg2"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of()),
                /* 8 */ Arguments.of(
                        new String[] {"p"},
                        List.of("player1", "player2", "player11",
                                "player22", "somePlayer"),
                        List.of("player1", "player2", "player11",
                                "player22")),
                /* 9 */ Arguments.of(
                        new String[] {"player1"},
                        List.of("player1", "player2", "player11",
                                "player22"),
                        List.of("player1", "player11")));
    }

    @Test
    void testParse_SelfPlayer() {
        var args = new String[] {};

        var result = commandInstance.parse(mockIssuerPlayer, args);

        assertNotNull(result);
        assertEquals(mockIssuerPlayer, result.who());
    }

    @Test
    void testParse_SelfNotPlayer() {
        var args = new String[] {};

        var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockSender), eq(Message.General.PLAYERS_ONLY));
    }

    @Test
    void testParse_OtherOnlinePlayer() {
        var args = new String[] {mockTargetPlayer.getName()};

        var result = commandInstance.parse(mockIssuerPlayer, args);

        assertNotNull(result);
        assertEquals(mockTargetPlayer, result.who());
    }

    @Test
    void testParse_OtherOfflinePlayer() {
        mockedBukkit.when(() -> Bukkit.getPlayerExact(mockTargetPlayer.getName()))
                .thenReturn(null);

        var args = new String[] {mockTargetPlayer.getName()};

        var result = commandInstance.parse(mockIssuerPlayer, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockIssuerPlayer),
                eq(Message.General.NOT_ONLINE_PLAYER), anyPlaceholder());
    }

    @Test
    void testParse_InvalidArgs() {
        var args = new String[] {"arg1", "arg2"};

        var result = commandInstance.parse(mockIssuerPlayer, args);

        assertNull(result);
        verify(mockLanguage).message(eq(mockIssuerPlayer),
                eq(Message.General.USAGE_INVALID), eq(Message.Teleport.USAGE_RTP));
    }

    @Test
    void testExecute_Fail() {
        var data = new RandomTeleportCommand.CommandData(mockTargetPlayer);

        // [1] fail and don't set cooldown
        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_FAIL));

        clearInvocations(mockLanguage);

        // [2] fail and don't set cooldown
        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 500L);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage, never()).message(
                eq(mockIssuerPlayer), eq(Message.Teleport.RTP_COOLDOWN),
                anyPlaceholder());
    }

    @Test
    void testExecute_Success() {
        var data = new RandomTeleportCommand.CommandData(mockTargetPlayer);

        when(mockRandomLocation.getX()).thenReturn(1D);
        when(mockRandomLocation.getY()).thenReturn(64D);
        when(mockRandomLocation.getZ()).thenReturn(1D);

        mockedWorldUtils
                .when(() -> WorldUtils.randomize(
                        any(Location.class),
                        any(WorldUtils.RandomLocationRestrictions.class)))
                .thenReturn(mockRandomLocation);

        // [1] success and set cooldown
        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(
                eq(mockIssuerPlayer), eq(Message.Teleport.RTP_OTHER), anyList());

        // [2] still on cooldown, cannot use command
        clearInvocations(mockLanguage, mockIssuerPlayer, mockTargetPlayer);

        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 500L);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        // [3] cooldown over, can use command
        clearInvocations(mockLanguage, mockIssuerPlayer, mockTargetPlayer);

        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 5000L);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(
                eq(mockIssuerPlayer), eq(Message.Teleport.RTP_OTHER), anyList());

        // [4] still on cooldown but can bypass
        clearInvocations(mockLanguage, mockIssuerPlayer, mockTargetPlayer);

        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 500L);

        when(mockIssuerPlayer.hasPermission("feathercore.teleport.random.bypass-cooldown"))
                .thenReturn(true);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockIssuerPlayer, data);
        });

        verify(mockLanguage).message(eq(mockIssuerPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(
                eq(mockIssuerPlayer), eq(Message.Teleport.RTP_OTHER), anyList());

        // [5] sender not player
        clearInvocations(mockLanguage, mockIssuerPlayer, mockTargetPlayer);

        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 500L);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockSender, data);
        });

        verify(mockLanguage).message(eq(mockSender), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(
                eq(mockSender), eq(Message.Teleport.RTP_OTHER), anyList());

        // [6] self teleport
        clearInvocations(mockLanguage, mockIssuerPlayer, mockTargetPlayer);

        mockedClock.when(Clock::currentTimeMillis).thenReturn(INITIAL_TIME + 500L);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockTargetPlayer, data);
        });

        verify(mockLanguage).message(eq(mockTargetPlayer), eq(Message.Teleport.RTP_TRY));
        verify(mockLanguage).message(
                eq(mockTargetPlayer), eq(Message.Teleport.RTP_SELF), anyList());
    }

}
