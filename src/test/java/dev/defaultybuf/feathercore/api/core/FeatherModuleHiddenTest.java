/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleHiddenTest.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @test_unit FeatherModuleHidden#0.6
 * @description Unit tests for FeatherModuleHidden
 */

package dev.defaultybuf.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feathercore.api.core.dummies.DummyModule;
import dev.defaultybuf.feathercore.modules.common.ModuleTestMocker;

class FeatherModuleHiddenTest extends ModuleTestMocker<DummyModule> {

    @Override
    protected Class<DummyModule> getModuleClass() {
        return DummyModule.class;
    }

    @Override
    protected String getModuleName() {
        return FeatherModule.HIDE_LIFECYCLE_PREFIX + "DummyModule";
    }

    @Test
    void basics() {
        assertDoesNotThrow(() -> {
            assertEquals(FeatherModule.HIDE_LIFECYCLE_PREFIX
                    + "DummyModule", moduleInstance.getModuleName());
            assertNotNull(moduleInstance.getConfig());
            moduleInstance.onEnable();
            moduleInstance.onDisable(moduleInstance.getLogger());
            verifyNoInteractions(dependenciesMap.get(IFeatherLogger.class));
        });
    }
}
