/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file CommandDependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Utility class for developing commands unit tests that use modules
 */

package dev.defaultybuf.feathercore.modules.common.mockers;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;

import dev.defaultybuf.feathercore.api.core.FeatherCommand;

public abstract class CommandTestMocker<CommandType extends FeatherCommand<?>>
        extends DependencyAccessorMocker {
    protected CommandType commandInstance;

    @BeforeEach
    void setUpCommandTest()
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        commandInstance = getCommandClass().getConstructor(FeatherCommand.InitData.class)
                .newInstance(new FeatherCommand.InitData(dependenciesMap));
    }

    protected abstract Class<CommandType> getCommandClass();
}
