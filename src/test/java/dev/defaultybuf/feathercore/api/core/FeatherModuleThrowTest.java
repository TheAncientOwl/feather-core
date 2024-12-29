/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleThrowTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit FeatherModuleThrow#0.6
 * @description Unit tests for FeatherModuleThrow
 */

package dev.defaultybuf.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.core.dummies.ThrowableDummyModule;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.common.ModuleTestMocker;

class FeatherModuleThrowTest extends ModuleTestMocker<ThrowableDummyModule> {
    @Override
    protected Class<ThrowableDummyModule> getModuleClass() {
        return ThrowableDummyModule.class;
    }

    @Override
    protected String getModuleName() {
        return "ThrowableDummyModule";
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
    void basics() {
        assertDoesNotThrow(() -> {
            assertEquals("ThrowableDummyModule", moduleInstance.getModuleName());
            assertNotNull(moduleInstance.getConfig());
        });

        assertThrows(FeatherSetupException.class, () -> {
            moduleInstance.onEnable();
        });

        assertDoesNotThrow(() -> {
            moduleInstance.onDisable(moduleInstance.getLogger());
        });
    }

}
