/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ListenerTestMocker.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class for developing listeners unit tests that use modules
 */
package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;

import mc.owls.valley.net.feathercore.api.core.FeatherListener;

public abstract class ListenerTestMocker<ListenerType extends FeatherListener>
        extends DependencyAccessorMocker {
    protected CommandSender mockSender;
    protected ListenerType listenerInstance;

    @BeforeEach
    void setUpListenerTest()
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        mockSender = mock(CommandSender.class);
        listenerInstance = getListenerClass().getConstructor(FeatherListener.InitData.class)
                .newInstance(new FeatherListener.InitData(dependenciesMap));
    }

    protected abstract Class<ListenerType> getListenerClass();
}
