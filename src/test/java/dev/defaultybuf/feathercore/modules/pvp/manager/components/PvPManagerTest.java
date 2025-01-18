/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPManagerTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit PvPManager#0.10
 * @description Unit tests for PvPManager
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;
import dev.defaultybuf.feather.toolkit.util.java.Clock;

class PvPManagerTest extends FeatherModuleTest<PvPManager> {
    @Mock Player mockVictim;
    @Mock Player mockAttacker;

    UUID victimUUID;
    UUID attackerUUID;

    @Override
    protected Class<PvPManager> getModuleClass() {
        return PvPManager.class;
    }

    @Override
    protected String getModuleName() {
        return "PvPManager";
    }

    @Override
    protected void setUp() {
        victimUUID = UUID.randomUUID();
        attackerUUID = UUID.randomUUID();

        lenient().when(mockVictim.getUniqueId()).thenReturn(victimUUID);
        lenient().when(mockAttacker.getUniqueId()).thenReturn(attackerUUID);

        lenient().when(mockServer.getPlayer(victimUUID)).thenReturn(mockVictim);
        lenient().when(mockServer.getPlayer(attackerUUID)).thenReturn(mockAttacker);
    }

    @Test
    void testLifeCycle() {
        assertDoesNotThrow(() -> {
            try (var mockedBukkit = mockStatic(Bukkit.class)) {
                var mockScheduler = mock(BukkitScheduler.class);
                mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

                moduleInstance.onModuleEnable();

                moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);
                assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
                assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

                verify(mockScheduler).runTaskTimerAsynchronously(eq(mockJavaPlugin),
                        any(Runnable.class), eq(0L), anyLong());

                moduleInstance.onModuleDisable();

                assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
                assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));
            }
        });
    }

    @Test
    void testIsPlayerInCombat() {
        assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
        assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));

        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));
    }

    @Test
    void testIsPlayerInCombatUUID() {
        assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
        assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));

        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim.getUniqueId()));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker.getUniqueId()));
    }

    @Test
    void testPutPlayersInCombat() {
        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

        verify(mockVictim, times(1)).setFlying(false);
        verify(mockVictim, times(1)).setAllowFlight(false);
        verify(mockAttacker, times(1)).setFlying(false);
        verify(mockAttacker, times(1)).setAllowFlight(false);

        clearInvocations(mockVictim, mockAttacker);

        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

        verify(mockVictim, never()).setFlying(false);
        verify(mockVictim, never()).setAllowFlight(false);
        verify(mockAttacker, never()).setFlying(false);
        verify(mockAttacker, never()).setAllowFlight(false);
    }

    @Test
    void testPutPlayersInCombat_OneBypass() {
        when(mockVictim.hasPermission("pvp.bypass.fly")).thenReturn(true);

        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

        verify(mockVictim, never()).setFlying(false);
        verify(mockVictim, never()).setAllowFlight(false);
        verify(mockAttacker, times(1)).setFlying(false);
        verify(mockAttacker, times(1)).setAllowFlight(false);
    }

    @Test
    void testPutPlayersInCombat_SamePlayer() {
        moduleInstance.putPlayersInCombat(mockVictim, mockVictim);

        assertFalse(moduleInstance.isPlayerInCombat(mockVictim));

        verify(mockVictim, never()).setFlying(false);
        verify(mockVictim, never()).setAllowFlight(false);
        verify(mockVictim, never()).sendMessage(anyString());
    }

    @Test
    void testRemovePlayerInCombat() {
        moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

        assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

        moduleInstance.removePlayerInCombat(mockVictim);

        assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
        assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

        moduleInstance.removePlayerInCombat(mockAttacker);

        assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
        assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));
    }

    @Test
    void testRemovePlayerInCombatByUUID() {
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(() -> Bukkit.getPlayer(victimUUID)).thenReturn(mockVictim);
            mockedBukkit.when(() -> Bukkit.getPlayer(attackerUUID)).thenReturn(mockAttacker);
            when(mockVictim.isOnline()).thenReturn(true);
            when(mockAttacker.isOnline()).thenReturn(true);

            moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

            assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
            assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

            moduleInstance.removePlayerInCombat(mockVictim.getUniqueId());

            assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
            assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

            moduleInstance.removePlayerInCombat(mockAttacker.getUniqueId());

            assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
            assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));
        }
    }

    @Test
    void testRemovePlayerInCombatByUUID_OneNull_OneNotOnline() {
        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(() -> Bukkit.getPlayer(victimUUID)).thenReturn(null);
            mockedBukkit.when(() -> Bukkit.getPlayer(attackerUUID)).thenReturn(mockAttacker);
            when(mockAttacker.isOnline()).thenReturn(false);

            moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

            assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
            assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

            moduleInstance.removePlayerInCombat(mockVictim.getUniqueId());

            assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
            assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

            moduleInstance.removePlayerInCombat(mockAttacker.getUniqueId());

            assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
            assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));
        }
    }

    @ParameterizedTest
    @MethodSource("provideWhitelistedCommandsData")
    void testGetWhitelistedCommands(List<String> configCommands, List<String> expectedCommands) {
        when(mockModuleConfig.getStringList("commands.whitelist")).thenReturn(configCommands);

        var whitelistedCommands = moduleInstance.getWhitelistedCommands();

        assertEquals(expectedCommands, whitelistedCommands);
    }

    static Stream<Arguments> provideWhitelistedCommandsData() {
        return Stream.of(
                Arguments.of(List.of("command1", "command2", "command3"),
                        List.of("command1", "command2", "command3")),
                Arguments.of(List.of(), List.of()),
                Arguments.of(null, null));
    }

    @Test
    void testCombatCheckerRun() {
        when(mockModuleConfig.getMillis("combat.time")).thenReturn(59000L);

        try (var mockedBukkit = mockStatic(Bukkit.class);
                var mockedClock = mockStatic(Clock.class)) {
            when(mockVictim.isOnline()).thenReturn(true);
            when(mockAttacker.isOnline()).thenReturn(true);
            mockedBukkit.when(() -> Bukkit.getPlayer(victimUUID)).thenReturn(mockVictim);
            mockedBukkit.when(() -> Bukkit.getPlayer(attackerUUID)).thenReturn(mockAttacker);

            var initialTime = 0L;

            mockedClock.when(Clock::currentTimeMillis).thenReturn(initialTime);

            moduleInstance.putPlayersInCombat(mockVictim, mockAttacker);

            var combatChecker = new PvPManager.CombatChecker(moduleInstance);
            combatChecker.run();

            assertTrue(moduleInstance.isPlayerInCombat(mockVictim));
            assertTrue(moduleInstance.isPlayerInCombat(mockAttacker));

            // Simulate 60s passing
            mockedClock.when(Clock::currentTimeMillis).thenReturn(initialTime + 60000L);
            combatChecker.run();

            assertFalse(moduleInstance.isPlayerInCombat(mockVictim));
            assertFalse(moduleInstance.isPlayerInCombat(mockAttacker));
        }
    }

}
