/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit FeatherCommand#0.4
 * @description Unit tests for FeatherCommand
 */

package mc.owls.valley.net.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.core.dummies.DummyCommand;
import mc.owls.valley.net.feathercore.modules.common.CommandTestMocker;

public class FeatherCommandTest extends CommandTestMocker<DummyCommand> {
    @Mock Command mockCommand;

    @Override
    protected Class<DummyCommand> getCommandClass() {
        return DummyCommand.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        return null;
    }

    @Override
    protected List<AutoCloseable> injectActualModules() {
        return null;
    }

    @Test
    void onTabComplete() {
        assertDoesNotThrow(() -> {
            String[] expectedCompletions = new String[] {"comp0", "comp1"};
            var completions = commandInstance.onTabComplete(mockSender, mockCommand, "alias",
                    expectedCompletions);
            assertEquals(Arrays.asList(expectedCompletions), completions);
        });
    }

    @Test
    void onCommand_01() {
        assertDoesNotThrow(() -> {
            assertTrue(
                    commandInstance.onCommand(mockSender, mockCommand, "label", new String[] {}));
        });
    }

    @Test
    void onCommand_02() {
        assertDoesNotThrow(() -> {
            assertTrue(
                    commandInstance.onCommand(mockSender, mockCommand, "label",
                            new String[] {"arg1"}));
        });
    }

    @Test
    void onCommand_03() {
        assertDoesNotThrow(() -> {
            assertTrue(
                    commandInstance.onCommand(mockSender, mockCommand, "label",
                            new String[] {"noperm"}));
        });
    }

}
