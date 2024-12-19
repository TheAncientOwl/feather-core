/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file CommandDependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class for developing commands unit tests that use modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;

public abstract class CommandDependencyAccessorMocker<CommandType>
        extends DependencyAccessorMocker {
    protected CommandSender mockSender;
    protected CommandType commandInstance;

    @BeforeEach
    void setUpCommandTest() {
        mockSender = mock(CommandSender.class);
        commandInstance = makeCommand();
    }

    protected abstract CommandType makeCommand();
}
