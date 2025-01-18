/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModuleTest.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @test_unit ReloadModule#0.4
 * @description Unit tests for ReloadModule
 */

package dev.defaultybuf.feather.toolkit.core.modules.reload.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feather.toolkit.testing.mockers.DependencyInjector;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;

class ReloadModuleTest extends FeatherModuleTest<ReloadModule> {

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
