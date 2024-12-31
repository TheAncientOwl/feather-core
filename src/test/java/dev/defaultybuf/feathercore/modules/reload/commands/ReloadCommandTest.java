/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @test_unit ReloadCommand#0.7
 * @description Unit tests for ReloadCommand
 */

package dev.defaultybuf.feathercore.modules.reload.commands;

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

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.modules.common.mockers.CommandTestMocker;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;

class ReloadCommandTest extends CommandTestMocker<ReloadCommand> {

        @Override
        protected Class<ReloadCommand> getCommandClass() {
                return ReloadCommand.class;
        }

        @Test
        void testHasPermission_WithPermission() {
                when(mockSender.hasPermission("feathercore.reload")).thenReturn(true);

                var commandData = new ReloadCommand.CommandData(new ArrayList<>());
                assertTrue(commandInstance.hasPermission(mockSender, commandData));

                verify(mockSender, times(1)).hasPermission("feathercore.reload");
                verifyNoInteractions(mockLanguage);
        }

        @Test
        void testHasPermission_WithoutPermission() {
                when(mockSender.hasPermission("feathercore.reload")).thenReturn(false);

                var commandData = new ReloadCommand.CommandData(new ArrayList<>());
                assertFalse(commandInstance.hasPermission(mockSender, commandData));

                verify(mockSender, times(1)).hasPermission("feathercore.reload");
                verify(mockLanguage, times(1))
                                .message(mockSender, Message.General.PERMISSION_DENIED);
        }

        @Test
        void testExecute_ReloadsConfigsAndTranslations() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                mock(LanguageManager.class));

                var commandData = new ReloadCommand.CommandData(enabledModules);
                commandInstance.execute(mockSender, commandData);

                verify(enabledModules.get(0).getConfig(), times(1)).reloadConfig();
                verify((LanguageManager) enabledModules.get(1), times(1)).reloadTranslations();
                verify(mockLanguage, times(1)).message(mockSender, Message.Reload.CONFIGS_RELOADED);
        }

        @Test
        void testParse_ValidAllArgument() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                DependencyInjector.Language.Mock(),
                                DependencyInjector.PlayersData.Mock());
                when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

                var args = new String[] {DependencyInjector.Reload.name()};
                var result = commandInstance.parse(mockSender, args);

                assertNotNull(result);
                assertEquals(1, result.modules().size());
                assertEquals(enabledModules.get(0), result.modules().get(0));
        }

        @Test
        void testParse_ValidArgument() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                DependencyInjector.Language.Mock(),
                                DependencyInjector.PlayersData.Mock());
                when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

                var args = new String[] {"all"};
                var result = commandInstance.parse(mockSender, args);

                assertNotNull(result);
                assertEquals(3, result.modules().size());
                assertEquals(enabledModules.get(0), result.modules().get(0));
                assertEquals(enabledModules.get(1), result.modules().get(1));
                assertEquals(enabledModules.get(2), result.modules().get(2));
        }

        @Test
        @SuppressWarnings("unchecked")
        void testParse_InvalidArgument() {
                when(commandInstance.getEnabledModules()).thenReturn(List.of());

                var args = new String[] {"nonexistent-module"};
                var result = commandInstance.parse(mockSender, args);

                assertNull(result);
                verify(mockLanguage, times(1))
                                .message(eq(mockSender), eq(Message.Reload.USAGE), any(Pair.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void testParse_EmptyArgument() {
                var args = new String[] {};
                var result = commandInstance.parse(mockSender, args);

                assertNull(result);
                verify(mockLanguage, times(1))
                                .message(eq(mockSender), eq(Message.Reload.USAGE), any(Pair.class));
        }

        @Test
        void testOnTabComplete_NoArgument() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                DependencyInjector.Language.Mock(),
                                DependencyInjector.PlayersData.Mock());
                when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

                var args = new String[] {};
                var completions = commandInstance.onTabComplete(args);

                assertEquals(4, completions.size());
                assertEquals("all", completions.get(0), "1st completion should be 'all'");
                assertEquals(DependencyInjector.Reload.name(), completions.get(1),
                                "2nd completion should be '" + DependencyInjector.Reload.name()
                                                + "'");
                assertEquals(DependencyInjector.Language.name(), completions.get(2),
                                "3rd completion should be '" + DependencyInjector.Language.name()
                                                + "'");
                assertEquals(DependencyInjector.PlayersData.name(), completions.get(3),
                                "4th completion should be '"
                                                + DependencyInjector.PlayersData.name() + "'");
        }

        @Test
        void testOnTabComplete_SingleArgument() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                DependencyInjector.Language.Mock(),
                                DependencyInjector.PlayersData.Mock());
                when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

                var args = new String[] {"re"};
                var completions = commandInstance.onTabComplete(args);

                assertEquals(1, completions.size());
                assertEquals(DependencyInjector.Reload.name(), completions.get(0),
                                "1st completion should be '" + DependencyInjector.Reload.name()
                                                + "'");
        }

        @Test
        void testOnTabComplete_NoMatchingArgument() {
                final var enabledModules = List.of(
                                DependencyInjector.Reload.Mock(),
                                DependencyInjector.Language.Mock(),
                                DependencyInjector.PlayersData.Mock());
                when(mockEnabledModulesProvider.getEnabledModules()).thenReturn(enabledModules);

                var args = new String[] {"unknown"};
                var completions = commandInstance.onTabComplete(args);

                assertTrue(completions.isEmpty());
        }

}
