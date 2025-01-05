/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @test_unit TeleportListener#0.6
 * @description Unit tests for TeleportListener
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherListenerTest;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

class TeleportListenerTest extends FeatherListenerTest<TeleportListener> {
    @Mock PlayerTeleportEvent mockEvent;
    @Mock Player mockPlayer;

    @MockedModule(of = Module.PvPManager) IPvPManager mockPvPManager;

    @Override
    protected Class<TeleportListener> getListenerClass() {
        return TeleportListener.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);
    }

    @Test
    void testOnPlayerTeleport_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnPlayerTeleport_NotInCombat() {
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(false);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
    }

    @Test
    void testOnPlayerTeleport_HasBypassPermission() {
        when(mockPlayer.hasPermission("pvp.bypass.teleport")).thenReturn(true);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockLanguage, never()).message(mockPlayer, Message.PvPManager.TELEPORT);
    }

    @Test
    void testOnPlayerTeleport_ChorusFruitNotBlocked() {
        when(mockEvent.getCause()).thenReturn(TeleportCause.CHORUS_FRUIT);
        when(mockPvPManager.getConfig().getBoolean("block-tp.chorus-fruit")).thenReturn(false);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockLanguage).message(mockPlayer, Message.PvPManager.TELEPORT);
    }

    @Test
    void testOnPlayerTeleport_EnderPearlNotBlocked() {
        when(mockEvent.getCause()).thenReturn(TeleportCause.ENDER_PEARL);
        when(mockPvPManager.getConfig().getBoolean("block-tp.ender-pearl")).thenReturn(false);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockLanguage).message(mockPlayer, Message.PvPManager.TELEPORT);
    }

    @Test
    void testOnPlayerTeleport_ChorusFruitBlocked() {
        when(mockEvent.getCause()).thenReturn(TeleportCause.CHORUS_FRUIT);
        when(mockPvPManager.getConfig().getBoolean("block-tp.chorus-fruit")).thenReturn(true);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockLanguage, never()).message(mockPlayer, Message.PvPManager.TELEPORT);
    }

    @Test
    void testOnPlayerTeleport_EnderPearlBlocked() {
        when(mockEvent.getCause()).thenReturn(TeleportCause.ENDER_PEARL);
        when(mockPvPManager.getConfig().getBoolean("block-tp.ender-pearl")).thenReturn(true);

        listenerInstance.onPlayerTeleport(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockLanguage, never()).message(mockPlayer, Message.PvPManager.TELEPORT);
    }
}
