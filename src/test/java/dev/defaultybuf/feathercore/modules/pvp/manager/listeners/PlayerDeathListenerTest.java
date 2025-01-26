/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDeathListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit PlayerDeathListener#0.4
 * @description Unit tests for PlayerDeathListener
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.testing.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feathercore.common.FeatherCoreDependencyFactory;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class PlayerDeathListenerTest extends FeatherListenerTest<PlayerDeathListener> {
    @Mock Player mockPlayer;
    @Mock PlayerDeathEvent mockEvent;

    @MockedModule IPvPManager mockPvPManager;

    @Override
    protected Class<PlayerDeathListener> getListenerClass() {
        return PlayerDeathListener.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockEvent.isCancelled()).thenReturn(false);
    }

    @Test
    void testOnPlayerDeath() {
        listenerInstance.onPlayerDeath(mockEvent);

        verify(mockPvPManager).removePlayerInCombat(mockPlayer);
    }

    @Test
    void testOnPlayerDeath_EventCancelled() {
        lenient().when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onPlayerDeath(mockEvent);

        verify(mockPvPManager, never()).removePlayerInCombat(mockPlayer);
    }
}
