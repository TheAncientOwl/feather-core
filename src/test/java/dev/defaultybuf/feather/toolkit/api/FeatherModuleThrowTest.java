/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModuleThrowTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit FeatherModuleThrow#0.6
 * @description Unit tests for FeatherModuleThrow
 */

package dev.defaultybuf.feather.toolkit.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.defaultybuf.feather.toolkit.api.dummies.ThrowableDummyModule;
import dev.defaultybuf.feather.toolkit.exceptions.FeatherSetupException;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherCoreDependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;

@InjectDependencies(factories = {FeatherCoreDependencyFactory.class})
class FeatherModuleThrowTest extends FeatherModuleTest<ThrowableDummyModule> {
    @Override
    protected Class<ThrowableDummyModule> getModuleClass() {
        return ThrowableDummyModule.class;
    }

    @Override
    protected String getModuleName() {
        return "ThrowableDummyModule";
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
