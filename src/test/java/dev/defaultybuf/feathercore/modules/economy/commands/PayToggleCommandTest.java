/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PayCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit PayCommand#0.10
 * @description Unit tests for PayCommand
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCommandTest;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;

class PayToggleCommandTest extends FeatherCommandTest<PayToggleCommand> {
    static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
     static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  command:\n" +
            "    invalid: 'Invalid command'\n" +
            "    no-permission: 'No permission'\n" +
            "    players-only: 'Only players can issue this command'\n" +
            "economy:\n" +
            "  pay:\n" +
            "    error:\n" +
            "      usage: 'Usage:'\n" +
            "  paytoggle:\n" +
            "    success:\n" +
            "      status-true: 'Changed to true'\n" +
            "      status-false: 'Changed to false'\n";
    // @formatter:on

    @Mock Player mockPlayer;
    @Mock CommandSender mockSender;

    @MockedModule IPlayersData mockPlayersData;

    @ActualModule(
            of = ILanguage.class,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    PlayerModel playerModel;

    @Override
    protected Class<PayToggleCommand> getCommandClass() {
        return PayToggleCommand.class;
    }

    @Override
    protected void setUp() {
        lenient().when(mockSender.hasPermission("feathercore.economy.general.paytoggle"))
                .thenReturn(true);
        lenient().when(mockPlayer.hasPermission("feathercore.economy.general.paytoggle"))
                .thenReturn(true);

        playerModel = new PlayerModel();
        playerModel.language = "en";
        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
    }

    @Test
    void testHasPermission_NoPermission() {
        when(mockPlayer.hasPermission("feathercore.economy.general.paytoggle")).thenReturn(false);

        var commandData = new PayToggleCommand.CommandData(playerModel);
        var result = commandInstance.hasPermission(mockPlayer, commandData);

        assertFalse(result);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testHasPermission_WithPermission() {
        when(mockPlayer.hasPermission("feathercore.economy.general.paytoggle")).thenReturn(true);

        var commandData = new PayToggleCommand.CommandData(playerModel);
        var result = commandInstance.hasPermission(mockPlayer, commandData);

        assertTrue(result);
    }

    @Test
    void testExecute_FalseToTrue() {
        var commandData = new PayToggleCommand.CommandData(playerModel);
        playerModel.acceptsPayments = false;

        commandInstance.execute(mockPlayer, commandData);

        assertTrue(playerModel.acceptsPayments);
        verify(mockPlayersData).markPlayerModelForSave(playerModel);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testExecute_TrueToFalse() {
        var commandData = new PayToggleCommand.CommandData(playerModel);
        playerModel.acceptsPayments = true;

        commandInstance.execute(mockPlayer, commandData);

        assertFalse(playerModel.acceptsPayments);
        verify(mockPlayersData).markPlayerModelForSave(playerModel);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_SenderNotPlayer() {
        var args = new String[] {};

        var commandData = commandInstance.parse(mockSender, args);

        assertNull(commandData);
        verify(mockSender).sendMessage(anyString());
    }

    @Test
    void testParse_InvalidArguments() {
        var args = new String[] {"invalid"};

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verify(mockPlayer).sendMessage(anyString());
    }

    @Test
    void testParse_ValidArguments() {
        var args = new String[] {};
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNotNull(commandData);
        assertEquals(playerModel, commandData.playerModel());
    }

    @Test
    void testParse_ValidArguments_NoModel() {
        var args = new String[] {};
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(null);

        var commandData = commandInstance.parse(mockPlayer, args);

        assertNull(commandData);
        verifyNoInteractions(mockPlayer);
    }

    @Test
    void testOnTabComplete() {
        var args = new String[] {};
        var completions = commandInstance.onTabComplete(args);

        assertTrue(completions.isEmpty());
    }
}
