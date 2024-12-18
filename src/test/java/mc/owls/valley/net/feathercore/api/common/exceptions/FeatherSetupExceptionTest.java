/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherSetupExceptionTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit FeatherSetupException#0.1
 * @description Unit tests for FeatherSetupException
 */

package mc.owls.valley.net.feathercore.api.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

class FeatherSetupExceptionTest {
    @Test
    void testNoArgConstructor() {
        FeatherSetupException exception = new FeatherSetupException();
        assertNull(exception.getMessage(), "Message should be null for no-arg constructor");
        assertNull(exception.getCause(), "Cause should be null for no-arg constructor");
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Custom error message";
        FeatherSetupException exception = new FeatherSetupException(message);

        assertEquals(message, exception.getMessage(), "Message should match the input message");
        assertNull(exception.getCause(), "Cause should be null when not provided");
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Custom error with cause";
        Throwable cause = new RuntimeException("Root cause");
        FeatherSetupException exception = new FeatherSetupException(message, cause);

        assertEquals(message, exception.getMessage(), "Message should match the input message");
        assertEquals(cause, exception.getCause(), "Cause should match the input cause");
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        FeatherSetupException exception = new FeatherSetupException(cause);

        assertEquals(cause, exception.getCause(), "Cause should match the input cause");
        assertEquals(cause.toString(), exception.getMessage(), "Message should match the cause's toString()");
    }
}
