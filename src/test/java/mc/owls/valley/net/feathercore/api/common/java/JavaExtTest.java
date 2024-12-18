/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file JavaExtTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit JavaExt#0.1
 * @description Unit tests for JavaExt
 */

package mc.owls.valley.net.feathercore.api.common.java;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class JavaExtTest {

    @Test
    void testFindIf() {
        // Test with a list and a matching predicate
        List<String> list = List.of("apple", "banana", "cherry");
        Predicate<String> startsWithB = s -> s.startsWith("b");
        Optional<String> result = JavaExt.findIf(list, startsWithB);
        assertTrue(result.isPresent());
        assertEquals("banana", result.get());

        // Test with no matching predicate
        Predicate<String> startsWithZ = s -> s.startsWith("z");
        result = JavaExt.findIf(list, startsWithZ);
        assertFalse(result.isPresent());

        // Test with an empty list
        result = JavaExt.findIf(List.of(), startsWithB);
        assertFalse(result.isPresent());

        // Test with null list
        result = JavaExt.findIf(null, startsWithB);
        assertFalse(result.isPresent());
    }

    @Test
    void testFindIndex() {
        // Test with a list and a matching predicate
        List<String> list = List.of("apple", "banana", "cherry");
        Predicate<String> startsWithB = s -> s.startsWith("b");
        Optional<Integer> result = JavaExt.findIndex(list, startsWithB);
        assertTrue(result.isPresent());
        assertEquals(1, result.get());

        // Test with no matching predicate
        Predicate<String> startsWithZ = s -> s.startsWith("z");
        result = JavaExt.findIndex(list, startsWithZ);
        assertFalse(result.isPresent());

        // Test with an empty list
        result = JavaExt.findIndex(List.of(), startsWithB);
        assertFalse(result.isPresent());

        // Test with null list
        result = JavaExt.findIndex(null, startsWithB);
        assertFalse(result.isPresent());
    }

    @Test
    void testFindLastIndex() {
        // Test with a list and a matching predicate
        List<String> list = List.of("apple", "banana", "apple", "cherry");
        Predicate<String> isApple = "apple"::equals;
        Optional<Integer> result = JavaExt.findLastIndex(list, isApple);
        assertTrue(result.isPresent());
        assertEquals(2, result.get());

        // Test with no matching predicate
        Predicate<String> startsWithZ = s -> s.startsWith("z");
        result = JavaExt.findLastIndex(list, startsWithZ);
        assertFalse(result.isPresent());

        // Test with an empty list
        result = JavaExt.findLastIndex(List.of(), isApple);
        assertFalse(result.isPresent());

        // Test with null list
        result = JavaExt.findLastIndex(null, isApple);
        assertFalse(result.isPresent());
    }

    @Test
    void testContains() {
        // Test with a list and a matching predicate
        List<String> list = List.of("apple", "banana", "cherry");
        Predicate<String> startsWithB = s -> s.startsWith("b");
        boolean result = JavaExt.contains(list, startsWithB);
        assertTrue(result);

        // Test with no matching predicate
        Predicate<String> startsWithZ = s -> s.startsWith("z");
        result = JavaExt.contains(list, startsWithZ);
        assertFalse(result);

        // Test with an empty list
        result = JavaExt.contains(List.of(), startsWithB);
        assertFalse(result);

        // Test with null list
        result = JavaExt.contains(null, startsWithB);
        assertFalse(result);
    }

    @Test
    void dummyConstructor() {
        @SuppressWarnings("unused") // JavaExt should contain only static methods
        JavaExt dummy = new JavaExt();
    }
}
