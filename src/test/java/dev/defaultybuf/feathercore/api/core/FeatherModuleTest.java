/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleImplTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit FeatherModule#0.6
 * @description Unit tests for FeatherModule
 */

package dev.defaultybuf.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feathercore.api.core.dummies.DummyModule;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherModuleTest;

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
