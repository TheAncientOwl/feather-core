/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModuleTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit ReloadModule#0.4
 * @description Unit tests for ReloadModule
 */

package mc.owls.valley.net.feathercore.modules.reload.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.modules.common.DependencyAccessorMocker;
import mc.owls.valley.net.feathercore.modules.common.ModuleMocks;

class ReloadModuleTest extends DependencyAccessorMocker {
    IConfigFile mockConfig = null;

    @BeforeEach
    void setUp() {
        mockConfig = mock(IConfigFile.class);
    }

    @Test
    void testModuleBasics() {
        var module = new ReloadModule(
                new FeatherModule.InitData(ModuleMocks.RELOAD_MODULE_NAME, () -> mockConfig,
                        dependenciesMap));
        assertDoesNotThrow(() -> module.onModuleEnable());
        assertDoesNotThrow(() -> module.onModuleDisable());
    }
}
