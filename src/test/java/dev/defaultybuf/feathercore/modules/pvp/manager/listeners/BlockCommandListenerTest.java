/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BlockCommandListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit BlockCommandListener#0.6
 * @description Unit tests for BlockCommandListener
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherListenerTest;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feathercore.common.FeatherCoreDependencyFactory;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class BlockCommandListenerTest extends FeatherListenerTest<BlockCommandsListener> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
     static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  command:\n" +
            "    no-permission: '&cYou do not have permission to execute this command.'\n" +
            "    invalid: '&cInvalid command usage.'\n" +
            "    players-only: '&cOnly players can execute this command.'\n" +
            "  not-player: '&c{0} is not a player.'\n" +
            "  not-valid-number: '&cNot valid number'\n" +
            "pvp:\n" +
            "  block-command: '&cYou cannot use this command during combat.'\n";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock PlayerCommandPreprocessEvent mockEvent;

    @MockedModule IPlayersData mockPlayersData;
    @MockedModule IPvPManager mockPvPManager;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    PlayerModel playerModel;

    @Override
    protected Class<BlockCommandsListener> getListenerClass() {
        return BlockCommandsListener.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
        lenient().when(mockEvent.isCancelled()).thenReturn(false);
        lenient().when(mockEvent.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(true);
        lenient().when(mockPvPManager.getWhitelistedCommands()).thenReturn(List.of("/whitelist"));
    }

    @Test
    void testOnCommand_EventCancelled() {
        lenient().when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onCommand(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnCommand_NotInCombat() {
        when(mockPvPManager.isPlayerInCombat(mockPlayer)).thenReturn(false);

        listenerInstance.onCommand(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnCommand_HasBypassPermission() {
        when(mockPlayer.hasPermission("pvp.bypass.commands")).thenReturn(true);

        listenerInstance.onCommand(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnCommand_WhitelistedCommand() {
        when(mockEvent.getMessage()).thenReturn("/whitelist");

        listenerInstance.onCommand(mockEvent);

        verify(mockEvent, never()).setCancelled(true);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testOnCommand_BlockCommand() {
        when(mockEvent.getMessage()).thenReturn("/blocked");

        listenerInstance.onCommand(mockEvent);

        verify(mockEvent).setCancelled(true);
        verify(mockPlayer).sendMessage(anyString());
    }
}
