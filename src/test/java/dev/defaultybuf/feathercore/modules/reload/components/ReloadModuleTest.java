/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModuleTest.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @test_unit ReloadModule#0.4
 * @description Unit tests for ReloadModule
 */

package dev.defaultybuf.feathercore.modules.reload.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector;
import dev.defaultybuf.feathercore.modules.common.mockers.ModuleTestMocker;

class ReloadModuleTest extends ModuleTestMocker<ReloadModule> {

    @Override
    protected Class<ReloadModule> getModuleClass() {
        return ReloadModule.class;
    }

    @Override
    protected String getModuleName() {
        return DependencyInjector.Reload.name();
    }

    @Test
    void testModuleBasics() {
        assertDoesNotThrow(() -> moduleInstance.onModuleEnable());
        assertDoesNotThrow(() -> moduleInstance.onModuleDisable());
    }

}
