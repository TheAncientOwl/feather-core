/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleHiddenTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit FeatherModuleHidden#0.6
 * @description Unit tests for FeatherModuleHidden
 */

package mc.owls.valley.net.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.core.dummies.DummyModule;
import mc.owls.valley.net.feathercore.modules.common.ModuleTestMocker;

class FeatherModuleHiddenTest extends ModuleTestMocker<DummyModule> {

    @Override
    protected Class<DummyModule> getModuleClass() {
        return DummyModule.class;
    }

    @Override
    protected String getModuleName() {
        return FeatherModule.HIDE_LIFECYCLE_PREFIX + "DummyModule";
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        return null;
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
