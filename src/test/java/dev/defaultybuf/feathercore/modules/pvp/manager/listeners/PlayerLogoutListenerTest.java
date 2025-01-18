/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerLogoutListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit PlayerLogoutListene#0.7
 * @description Unit tests for PlayerLogoutListene
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.mockers.DependencyInjector.Module;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherListenerTest;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

class PlayerLogoutListenerTest extends FeatherListenerTest<PlayerLogoutListener> {
    @Mock Player mockPlayer;
    @Mock PlayerQuitEvent mockEvent;

    @MockedModule(of = Module.PlayersData) IPlayersData mockpPlayersData;
    @MockedModule(of = Module.PvPManager) IPvPManager mockPvPManager;

    @Override
    protected Class<PlayerLogoutListener> getListenerClass() {
        return PlayerLogoutListener.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockPvPManager.getConfig().getBoolean("on-logout.kill")).thenReturn(true);

        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
    }

    @Test
    void testOnPlayerLogout_NotInCombat() {
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(false);

        listenerInstance.onPlayerLogout(mockEvent);

        verify(mockPlayer, never()).setHealth(0.0d);
        verify(mockPvPManager, never()).removePlayerInCombat(mockPlayer);
    }

    @Test
    void testOnPlayerLogout_InCombat_Kill() {
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);

        listenerInstance.onPlayerLogout(mockEvent);

        verify(mockPlayer).setHealth(0.0d);
        verify(mockPvPManager).removePlayerInCombat(mockPlayer);
    }

    @Test
    void testOnPlayerLogout_InCombat_KillBypass() {
        when(mockPlayer.hasPermission("pvp.bypass.killonlogout")).thenReturn(true);
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);

        listenerInstance.onPlayerLogout(mockEvent);

        verify(mockPlayer, never()).setHealth(0.0d);
        verify(mockPvPManager, never()).removePlayerInCombat(mockPlayer);
    }

    @Test
    void testOnPlayerLogout_InCombat_DontKill() {
        lenient().when(mockPvPManager.getConfig().getBoolean("on-logout.kill")).thenReturn(false);
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);

        listenerInstance.onPlayerLogout(mockEvent);

        verify(mockPlayer, never()).setHealth(0.0d);
        verify(mockPvPManager, never()).removePlayerInCombat(mockPlayer);
    }

    @Test
    void testOnPlayerLogout_InCombat_Broadcast() {
        var mockOnlinePlayer = mock(Player.class);

        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);
        when(mockPvPManager.getConfig().getBoolean("on-logout.broadcast")).thenReturn(true);

        try (var mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockOnlinePlayer));

            listenerInstance.onPlayerLogout(mockEvent);

            verify(mockPlayer).setHealth(0.0d);
            verify(mockPvPManager).removePlayerInCombat(mockPlayer);
            verify(mockLanguage).message(eq(mockOnlinePlayer), anyString(), anyPlaceholder());
        }
    }
}
