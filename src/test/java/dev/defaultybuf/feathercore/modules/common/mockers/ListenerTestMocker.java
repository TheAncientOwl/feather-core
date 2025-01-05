/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ListenerTestMocker.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Utility class for developing listeners unit tests that use modules
 */
package dev.defaultybuf.feathercore.modules.common.mockers;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;

import dev.defaultybuf.feathercore.api.core.FeatherListener;

public abstract class ListenerTestMocker<ListenerType extends FeatherListener>
        extends DependencyAccessorMocker {
    protected ListenerType listenerInstance;

    @BeforeEach
    void setUpListenerTest()
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        listenerInstance = getListenerClass().getConstructor(FeatherListener.InitData.class)
                .newInstance(new FeatherListener.InitData(dependenciesMap));
    }

    protected abstract Class<ListenerType> getListenerClass();
}
