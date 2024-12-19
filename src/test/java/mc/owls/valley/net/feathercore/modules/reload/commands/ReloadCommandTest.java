/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit ReloadCommand#0.7
 * @description Unit tests for ReloadCommand
 */

package mc.owls.valley.net.feathercore.modules.reload.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.modules.common.CommandDependencyAccessorMocker;
import mc.owls.valley.net.feathercore.modules.common.ModuleMocks;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;

class ReloadCommandTest extends CommandDependencyAccessorMocker<ReloadCommand> {

    @Override
    protected ReloadCommand makeCommand() {
        return new ReloadCommand(new FeatherCommand.InitData(dependenciesMap));
    }

    @Test
    void testHasPermission_WithPermission() {
        when(mockSender.hasPermission("feathercore.reload")).thenReturn(true);

        final var commandData = new ReloadCommand.CommandData(new ArrayList<>());
        assertTrue(commandInstance.hasPermission(mockSender, commandData));

        verify(mockSender, times(1)).hasPermission("feathercore.reload");
        verifyNoInteractions(mockLanguage); // Ensure no error message is sent
    }

    @Test
    void testHasPermission_WithoutPermission() {
        when(mockSender.hasPermission("feathercore.reload")).thenReturn(false);

        final var commandData = new ReloadCommand.CommandData(new ArrayList<>());
        assertFalse(commandInstance.hasPermission(mockSender, commandData));

        verify(mockSender, times(1)).hasPermission("feathercore.reload");
        verify(mockLanguage, times(1))
                .message(mockSender, Message.General.PERMISSION_DENIED);
    }

    @Test
    void testExecute_ReloadsConfigsAndTranslations() {
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule(), mock(LanguageManager.class));
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var commandData = new ReloadCommand.CommandData(enabledModules);
        commandInstance.execute(mockSender, commandData);

        verify(enabledModules.get(0).getConfig(), times(1)).reloadConfig();
        verify((LanguageManager) enabledModules.get(1), times(1)).reloadTranslations();
        verify(mockLanguage, times(1)).message(mockSender, Message.Reload.CONFIGS_RELOADED);
    }

    @Test
    void testParse_ValidAllArgument() {
        // TODO: Add more modules when mocks implemented
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule());
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var args = new String[] { ModuleMocks.RELOAD_MODULE_NAME };
        final var result = commandInstance.parse(mockSender, args);

        assertNotNull(result);
        assertEquals(1, result.modules().size());
        assertEquals(enabledModules.get(0), result.modules().get(0));
    }

    @Test
    void testParse_ValidArgument() {
        // TODO: Add more modules when mocks implemented
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule());
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var args = new String[] { "all" };
        final var result = commandInstance.parse(mockSender, args);

        assertNotNull(result);
        assertEquals(1, result.modules().size());
        assertEquals(enabledModules.get(0), result.modules().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParse_InvalidArgument() {
        when(commandInstance.getEnabledModules()).thenReturn(List.of());

        final var args = new String[] { "nonexistent-module" };
        final var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockLanguage, times(1))
                .message(eq(mockSender), eq(Message.Reload.USAGE), any(Pair.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParse_EmptyArgument() {
        final var args = new String[] {};
        final var result = commandInstance.parse(mockSender, args);

        assertNull(result);
        verify(mockLanguage, times(1))
                .message(eq(mockSender), eq(Message.Reload.USAGE), any(Pair.class));
    }

    @Test
    void testOnTabComplete_NoArgument() {
        // TODO: Add more modules when mocks implemented
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule());
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var args = new String[] {};
        final var completions = commandInstance.onTabComplete(args);

        assertEquals(2, completions.size());
        assertEquals("all", completions.get(0), "1st completion should be 'all'");
        assertEquals(ModuleMocks.RELOAD_MODULE_NAME, completions.get(1),
                "2nd completion should be '" + ModuleMocks.RELOAD_MODULE_NAME + "'");
    }

    @Test
    void testOnTabComplete_SingleArgument() {
        // TODO: Add more modules when mocks implemented
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule());
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var args = new String[] { "re" };
        final var completions = commandInstance.onTabComplete(args);

        assertEquals(1, completions.size());
        assertEquals(ModuleMocks.RELOAD_MODULE_NAME, completions.get(0),
                "1st completion should be '" + ModuleMocks.RELOAD_MODULE_NAME + "'");
    }

    @Test
    void testOnTabComplete_NoMatchingArgument() {
        // TODO: Add more modules when mocks implemented
        final List<FeatherModule> enabledModules = List.of(ModuleMocks.ReloadModule());
        when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

        final var args = new String[] { "unknown" };
        final var completions = commandInstance.onTabComplete(args);

        assertTrue(completions.isEmpty());
    }
}
