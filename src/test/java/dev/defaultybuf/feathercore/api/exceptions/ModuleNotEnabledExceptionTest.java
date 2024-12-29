/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModuleNotEnabledExceptionTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit ModuleNotEnabledException#0.1
 * @description Unit tests for ModuleNotEnabledException
 */

package dev.defaultybuf.feathercore.api.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ModuleNotEnabledExceptionTest {

    @Test
    void testNoArgConstructor() {
        ModuleNotEnabledException exception = new ModuleNotEnabledException();
        assertNull(exception.getMessage(), "Message should be null for no-arg constructor");
    }

    @Test
    void testConstructorWithModuleName() {
        String moduleName = "TestModule";
        ModuleNotEnabledException exception = new ModuleNotEnabledException(moduleName);

        assertEquals("Module '" + moduleName + "' is not enabled", exception.getMessage(),
                "Message should match the expected format");
    }

    @Test
    void testConstructorWithEmptyModuleName() {
        String moduleName = "";
        ModuleNotEnabledException exception = new ModuleNotEnabledException(moduleName);

        assertEquals("Module '' is not enabled", exception.getMessage(),
                "Message should handle an empty module name correctly");
    }

    @Test
    void testConstructorWithNullModuleName() {
        ModuleNotEnabledException exception = new ModuleNotEnabledException(null);

        assertEquals("Module 'null' is not enabled", exception.getMessage(),
                "Message should handle a null module name correctly");
    }
}
