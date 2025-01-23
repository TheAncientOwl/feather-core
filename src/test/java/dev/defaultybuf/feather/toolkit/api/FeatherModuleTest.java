/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleImplTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit FeatherModule#0.6
 * @description Unit tests for FeatherModule
 */

package dev.defaultybuf.feather.toolkit.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feather.toolkit.api.dummies.DummyModule;
import dev.defaultybuf.feather.toolkit.api.interfaces.IFeatherLogger;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class FeatherModuleImplTest extends FeatherModuleTest<DummyModule> {

    @Override
    protected Class<DummyModule> getModuleClass() {
        return DummyModule.class;
    }

    @Override
    protected String getModuleName() {
        return "DummyModule";
    }

    @Test
    void basics() {
        assertDoesNotThrow(() -> {
            assertEquals("DummyModule", moduleInstance.getModuleName());
            assertNotNull(moduleInstance.getConfig());
            moduleInstance.onEnable();
            moduleInstance.onDisable(moduleInstance.getLogger());
            verify((IFeatherLogger) dependenciesMap.get(IFeatherLogger.class), times(4))
                    .info(anyString());
        });
    }

}
