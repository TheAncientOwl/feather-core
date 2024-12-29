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

package dev.defaultybuf.feathercore.modules.common;

import static org.mockito.Mockito.mock;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;

import dev.defaultybuf.feathercore.api.core.FeatherCommand;

public abstract class CommandTestMocker<CommandType extends FeatherCommand<?>>
        extends DependencyAccessorMocker {
    protected CommandSender mockSender;
    protected CommandType commandInstance;

    @BeforeEach
    void setUpCommandTest()
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        mockSender = mock(CommandSender.class);
        commandInstance = getCommandClass().getConstructor(FeatherCommand.InitData.class)
                .newInstance(new FeatherCommand.InitData(dependenciesMap));
    }

    protected abstract Class<CommandType> getCommandClass();
}
