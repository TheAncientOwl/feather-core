/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file MoveCancelTpListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @test_unit MoveCancelTpListener#0.5
 * @description Unit tests for MoveCancelTpListener
 */

package dev.defaultybuf.feathercore.modules.teleport.listeners;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feathercore.common.FeatherCoreDependencyFactory;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class MoveCancelTpListenerTest extends FeatherListenerTest<MoveCancelTpListener> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
     static final String EN_LANGUAGE_FILE_CONTENT =
            "teleport:\n" +
            "  request:\n" +
            "    moved-while-waiting: '&cYou moved while waiting for teleportation!'\n";
    // @formatter:on

    @Mock PlayerMoveEvent mockEvent;
    @Mock Player mockPlayer;
    @Mock Location mockTo;
    @Mock Location mockFrom;

    @MockedModule IPlayersData mockPlayersData;
    @MockedModule ITeleport mockTeleport;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    PlayerModel playerModel;

    @Override
    protected Class<MoveCancelTpListener> getListenerClass() {
        return MoveCancelTpListener.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockEvent.getFrom()).thenReturn(mockFrom);
        lenient().when(mockEvent.getTo()).thenReturn(mockTo);
        lenient().when(mockFrom.getX()).thenReturn(0.0);
        lenient().when(mockFrom.getY()).thenReturn(0.0);
        lenient().when(mockFrom.getZ()).thenReturn(0.0);
        lenient().when(mockTo.getX()).thenReturn(0.0);
        lenient().when(mockTo.getY()).thenReturn(0.0);
        lenient().when(mockTo.getZ()).thenReturn(0.0);
        lenient().when(mockTeleport.cancelTeleport(mockPlayer)).thenReturn(true);
    }

    @Test
    void testOnPlayerMove_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport, never()).cancelTeleport(mockPlayer);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnPlayerMove_NoMovement() {
        when(mockEvent.isCancelled()).thenReturn(false);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport, never()).cancelTeleport(mockPlayer);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnPlayerMove_MovementDetected_X() {
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockTo.getX()).thenReturn(1.0);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport).cancelTeleport(mockPlayer);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnPlayerMove_MovementDetected_Y() {
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockTo.getY()).thenReturn(1.0);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport).cancelTeleport(mockPlayer);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnPlayerMove_MovementDetected_Z() {
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockTo.getZ()).thenReturn(1.0);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport).cancelTeleport(mockPlayer);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testOnPlayerMove_TeleportNotCancelled() {
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockTo.getX()).thenReturn(1.0);
        when(mockTeleport.cancelTeleport(mockPlayer)).thenReturn(false);

        listenerInstance.onPlayerMove(mockEvent);

        verify(mockTeleport).cancelTeleport(mockPlayer);
        verify(mockPlayer, never()).sendMessage(anyString());
    }
}
