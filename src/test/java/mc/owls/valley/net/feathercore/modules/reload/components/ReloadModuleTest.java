/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModuleTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit ReloadModule#0.4
 * @description Unit tests for ReloadModule
 */

package mc.owls.valley.net.feathercore.modules.reload.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.modules.common.ModuleTestMocker;
import mc.owls.valley.net.feathercore.modules.common.Modules;

class ReloadModuleTest extends ModuleTestMocker<ReloadModule> {

    @Override
    protected Class<ReloadModule> getModuleClass() {
        return ReloadModule.class;
    }

    @Override
    protected String getModuleName() {
        return Modules.RELOAD.name();
    }

    @Override
    protected List<AutoCloseable> injectActualModules() {
        return null;
    }

    @Test
    void testModuleBasics() {
        assertDoesNotThrow(() -> moduleInstance.onModuleEnable());
        assertDoesNotThrow(() -> moduleInstance.onModuleDisable());
    }

}
