/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModuleTest.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @test_unit ReloadModule#0.4
 * @description Unit tests for ReloadModule
 */

package dev.defaultybuf.feather.toolkit.core.modules.reload.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherToolkitDependencyFactory;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class ReloadModuleTest extends FeatherModuleTest<ReloadModule> {

    @Override
    protected Class<ReloadModule> getModuleClass() {
        return ReloadModule.class;
    }

    @Override
    protected String getModuleName() {
        return FeatherToolkitDependencyFactory.getReloadFactory().name();
    }

    @Test
    void testModuleBasics() {
        assertDoesNotThrow(() -> moduleInstance.onModuleEnable());
        assertDoesNotThrow(() -> moduleInstance.onModuleDisable());
    }

}
