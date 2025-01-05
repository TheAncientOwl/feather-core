/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit Teleport#0.8
 * @description Unit tests for Teleport
 */

package dev.defaultybuf.feathercore.modules.teleport.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import dev.defaultybuf.feathercore.api.common.util.Clock;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherModuleTest;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestStatus;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestType;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.TeleportChecker;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.TeleportRequest;

class TeleportTest extends FeatherModuleTest<Teleport> {
    @Mock World mockWorld;
    @Mock Player mockIssuer;
    @Mock Player mockTarget;
    @Mock BukkitTask mockTask;
    @Mock BukkitScheduler mockScheduler;

    @TestField UUID issuerUUID;
    @TestField UUID targetUUID;

    @TestField Location location;

    @Override
    protected Class<Teleport> getModuleClass() {
        return Teleport.class;
    }

    @Override
    protected String getModuleName() {
        return "Teleport";
    }

    @Override
    protected void setUp() {
        issuerUUID = UUID.randomUUID();
        targetUUID = UUID.randomUUID();

        location = new Location(mockWorld, 0, 64, 0, 5, 5);

        lenient().when(mockIssuer.getUniqueId()).thenReturn(issuerUUID);
        lenient().when(mockTarget.getUniqueId()).thenReturn(targetUUID);

        lenient().when(mockIssuer.getWorld()).thenReturn(mockWorld);
        lenient().when(mockTarget.getWorld()).thenReturn(mockWorld);

        lenient().when(mockIssuer.getLocation()).thenReturn(location);
        lenient().when(mockTarget.getLocation()).thenReturn(location);

        lenient()
                .when(mockScheduler.runTask(any(Plugin.class), any(Runnable.class)))
                .thenAnswer(new Answer<BukkitTask>() {
                    @Override
                    public BukkitTask answer(InvocationOnMock invocation) {
                        Runnable task = invocation.getArgument(1);
                        task.run();
                        return mockTask;
                    }
                });
    }

    @Test
    void testLifeCycle() {
        assertDoesNotThrow(() -> {
            assertDoesNotThrow(() -> {
                try (var mockedBukkit = mockStatic(Bukkit.class)) {
                    var mockScheduler = mock(BukkitScheduler.class);
                    mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

                    moduleInstance.onModuleEnable();

                    verify(mockScheduler).runTaskTimerAsynchronously(eq(mockJavaPlugin),
                            any(Runnable.class), eq(0L), anyLong());

                    moduleInstance.onModuleDisable();
                }
            });
        });
    }

    @Test
    void testTeleportPlayerToPlayer() {
        moduleInstance.teleport(mockIssuer, mockTarget);

        verify(mockIssuer).teleport(mockTarget);
    }

    @Test
    void testTeleportPlayerToLocation() {
        moduleInstance.teleport(mockIssuer, location);

        verify(mockIssuer).teleport(location);
    }

    @Test
    void testTeleportPlayerToCoordinates() {
        moduleInstance.teleport(mockIssuer, 100, 64, 100, mockWorld);

        verify(mockIssuer).teleport(
                new Location(mockWorld, 100, 64, 100, location.getYaw(), location.getPitch()));

        clearInvocations(mockIssuer, mockWorld);

        moduleInstance.teleport(mockIssuer, 100, 64, 100);

        verify(mockIssuer).teleport(
                new Location(mockWorld, 100, 64, 100, location.getYaw(), location.getPitch()));
    }

    @Test
    void testRequest() {
        var result = moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.REQUESTED, result);

        result = moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.ALREADY_REQUESTED, result);
    }

    @Test
    void testCancelRequest() {
        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        var result = moduleInstance.cancelRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.CANCELLED, result);
    }

    @Test
    void testCancelRequest_NoSuchRequest() {
        var result = moduleInstance.cancelRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.NO_SUCH_REQUEST, result);
    }

    @Test
    void testAcceptRequest() {
        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        var result = moduleInstance.acceptRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.ACCEPTED, result);
    }

    @Test
    void testCancelRequest_PlayerPlayer() {
        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        var result = moduleInstance.cancelRequest(mockIssuer, mockTarget);

        assertEquals(Teleport.RequestStatus.CANCELLED, result);
    }

    @Test
    void testCancelRequest_PlayerPlayer_NoSuchRequest() {
        var result = moduleInstance.cancelRequest(mockIssuer, mockTarget);

        assertEquals(Teleport.RequestStatus.NO_SUCH_REQUEST, result);
    }

    @Test
    void testAcceptRequest_Issuer_Target() {
        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);

        var result = moduleInstance.acceptRequest(mockIssuer, mockTarget);

        assertEquals(Teleport.RequestStatus.ACCEPTED, result);

        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.HERE);

        result = moduleInstance.acceptRequest(mockIssuer, mockTarget);

        assertEquals(Teleport.RequestStatus.ACCEPTED, result);
    }

    @Test
    void testAcceptRequest_NoSuchRequest() {
        var result = moduleInstance.acceptRequest(mockIssuer, mockTarget);

        assertEquals(Teleport.RequestStatus.NO_SUCH_REQUEST, result);
    }

    @Test
    void testAcceptRequest_NoSuchRequestTyped() {
        var result = moduleInstance.acceptRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);

        assertEquals(Teleport.RequestStatus.NO_SUCH_REQUEST, result);
    }

    @Test
    void testIsWaitingForTeleport() {
        var status = moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        assertEquals(RequestStatus.REQUESTED, status);

        status = moduleInstance.acceptRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);
        assertEquals(RequestStatus.ACCEPTED, status);

        moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        var result = moduleInstance.isWaitingForTeleport(mockIssuer);

        assertTrue(result);
    }

    @Test
    void testCancelTeleport() {
        var status = moduleInstance.request(mockIssuer, mockTarget, Teleport.RequestType.TO);
        assertEquals(RequestStatus.REQUESTED, status);

        status = moduleInstance.acceptRequest(mockIssuer, mockTarget, Teleport.RequestType.TO);
        assertEquals(RequestStatus.ACCEPTED, status);

        var result = moduleInstance.cancelTeleport(mockIssuer);

        assertTrue(result);
    }

    @Test
    void testCancelTeleport_NoRequest() {
        var result = moduleInstance.cancelTeleport(mockIssuer);

        assertFalse(result);
    }

    @Test
    void testTeleportRequestUpdateTime() {
        var teleportRequest = new TeleportRequest(mockIssuer, mockTarget, RequestType.TO,
                System.currentTimeMillis());
        long newTime = System.currentTimeMillis() + 1000;
        teleportRequest.updateTime(newTime);

        assertFalse(teleportRequest.isExpired(1000, newTime - 1));
        assertFalse(teleportRequest.isExpired(1000, newTime));
        assertFalse(teleportRequest.isExpired(1000, newTime + 1));
        assertFalse(teleportRequest.isExpired(1000, newTime + 10));
        assertFalse(teleportRequest.isExpired(1000, newTime + 1000));
        assertTrue(teleportRequest.isExpired(1000, newTime + 1001));
    }

    @Test
    void testTeleportRequestIsExpired() {
        var teleportRequest = new TeleportRequest(mockIssuer, mockTarget, RequestType.TO,
                System.currentTimeMillis());
        long lifetime = 1000;
        long now = System.currentTimeMillis();

        assertFalse(teleportRequest.isExpired(lifetime, now));

        teleportRequest.updateTime(now - lifetime - 1);
        assertTrue(teleportRequest.isExpired(lifetime, now));
    }

    @Test
    void testTeleportRequestIs() {
        var teleportRequest = new TeleportRequest(mockIssuer, mockTarget, RequestType.TO,
                System.currentTimeMillis());
        assertTrue(teleportRequest.is(RequestType.TO));
        assertFalse(teleportRequest.is(RequestType.HERE));
    }

    @Test
    void testTeleportRequestEquals() {
        var teleportRequest = new TeleportRequest(mockIssuer, mockTarget, RequestType.TO,
                System.currentTimeMillis());
        assertTrue(teleportRequest.equals(mockIssuer, mockTarget, RequestType.TO));
        assertFalse(teleportRequest.equals(mockIssuer, mockTarget, RequestType.HERE));

        var differentIssuer = mock(Player.class);
        when(differentIssuer.getUniqueId()).thenReturn(UUID.randomUUID());
        assertFalse(teleportRequest.equals(differentIssuer, mockTarget, RequestType.TO));

        var differentTarget = mock(Player.class);
        when(differentTarget.getUniqueId()).thenReturn(UUID.randomUUID());
        assertFalse(teleportRequest.equals(mockIssuer, differentTarget, RequestType.TO));
    }

    @Test
    void testTeleportRequestEqualsPlayers() {
        var teleportRequest = new TeleportRequest(mockIssuer, mockTarget, RequestType.TO,
                System.currentTimeMillis());
        assertTrue(teleportRequest.equals(mockIssuer, mockTarget));
        assertFalse(teleportRequest.equals(mockIssuer, mock(Player.class)));

        var differentIssuer = mock(Player.class);
        when(differentIssuer.getUniqueId()).thenReturn(UUID.randomUUID());
        assertFalse(teleportRequest.equals(differentIssuer, mockTarget));

        var differentTarget = mock(Player.class);
        when(differentTarget.getUniqueId()).thenReturn(UUID.randomUUID());
        assertFalse(teleportRequest.equals(mockIssuer, differentTarget));
    }

    @Test
    void testTeleportCheckerRun() {
        when(mockModuleConfig.getMillis("request.accept-delay")).thenReturn(5000L);

        try (var mockedBukkit = mockStatic(Bukkit.class);
                var mockedClock = mockStatic(Clock.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(issuerUUID)).thenReturn(mockIssuer);
            mockedBukkit.when(() -> Bukkit.getPlayer(targetUUID)).thenReturn(mockTarget);
            mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

            var initialTime = 0L;
            mockedClock.when(Clock::currentTimeMillis).thenReturn(initialTime);

            var teleportChecker = new TeleportChecker(moduleInstance);
            teleportChecker.run();

            assertEquals(RequestStatus.REQUESTED,
                    moduleInstance.request(mockIssuer, mockTarget, RequestType.TO));
            assertEquals(RequestStatus.ACCEPTED,
                    moduleInstance.acceptRequest(mockIssuer, mockTarget, RequestType.TO));

            assertEquals(RequestStatus.REQUESTED,
                    moduleInstance.request(mockIssuer, mockTarget, RequestType.HERE));
            assertEquals(RequestStatus.ACCEPTED,
                    moduleInstance.acceptRequest(mockIssuer, mockTarget, RequestType.HERE));

            teleportChecker.run();

            verify(mockIssuer, never()).teleport(mockTarget);
            verify(mockTarget, never()).teleport(mockTarget);
            assertTrue(moduleInstance.isWaitingForTeleport(mockIssuer));
            assertTrue(moduleInstance.isWaitingForTeleport(mockTarget));
        }
    }

    @Test
    void testTeleportCheckerRun_Expired() {
        when(mockModuleConfig.getMillis("request.accept-delay")).thenReturn(5000L);

        try (var mockedBukkit = mockStatic(Bukkit.class);
                var mockedClock = mockStatic(Clock.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(issuerUUID)).thenReturn(mockIssuer);
            mockedBukkit.when(() -> Bukkit.getPlayer(targetUUID)).thenReturn(mockTarget);
            mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

            var initialTime = 0L;

            mockedClock.when(Clock::currentTimeMillis).thenReturn(initialTime);

            var teleportChecker = new TeleportChecker(moduleInstance);
            teleportChecker.run();

            assertEquals(RequestStatus.REQUESTED,
                    moduleInstance.request(mockIssuer, mockTarget, RequestType.TO));
            assertEquals(RequestStatus.ACCEPTED,
                    moduleInstance.acceptRequest(mockIssuer, mockTarget, RequestType.TO));

            assertEquals(RequestStatus.REQUESTED,
                    moduleInstance.request(mockIssuer, mockTarget, RequestType.HERE));
            assertEquals(RequestStatus.ACCEPTED,
                    moduleInstance.acceptRequest(mockIssuer, mockTarget, RequestType.HERE));

            var expiredTime = initialTime + 6000L;
            mockedClock.when(Clock::currentTimeMillis).thenReturn(expiredTime);

            teleportChecker.run();

            verify(mockIssuer).teleport(mockTarget);
            verify(mockTarget).teleport(mockIssuer);
            assertFalse(moduleInstance.isWaitingForTeleport(mockIssuer));
            assertFalse(moduleInstance.isWaitingForTeleport(mockTarget));
        }
    }
}
