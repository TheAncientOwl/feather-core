/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file CacheTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit Cache#0.2
 * @description Unit tests for Cache
 */

package dev.defaultybuf.feathercore.api.common.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class CacheTest {

    @Test
    void testCacheReturnsSameValue() {
        // Supplier returns a constant value
        Supplier<String> supplier = () -> "test";
        Cache<String> cache = Cache.of(supplier);

        String firstCall = cache.get();
        String secondCall = cache.get();

        // Assert both calls return the same object
        assertEquals("test", firstCall);
        assertSame(firstCall, secondCall);
    }

    @Test
    void testCacheInitializesLazily() {
        // Supplier increments an AtomicInteger to track calls
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<Integer> supplier = counter::incrementAndGet;
        Cache<Integer> cache = Cache.of(supplier);

        // Supplier should not be called until get() is invoked
        assertEquals(0, counter.get());

        // First get() should call the supplier
        Integer value = cache.get();
        assertEquals(1, counter.get());
        assertEquals(1, value);

        // Subsequent calls should not call the supplier
        value = cache.get();
        assertEquals(1, counter.get());
        assertEquals(1, value);
    }

    @Test
    void testCacheHandlesNullSupplierResult() {
        // Supplier returns null
        Supplier<Object> supplier = () -> null;
        Cache<Object> cache = Cache.of(supplier);

        Object firstCall = cache.get();
        Object secondCall = cache.get();

        // Assert both calls return null
        assertNull(firstCall);
        assertNull(secondCall);

        // Assert both calls return the same null reference
        assertSame(firstCall, secondCall);
    }

    @Test
    void testCacheWorksWithDifferentTypes() {
        // Test with an Integer supplier
        Supplier<Integer> intSupplier = () -> 42;
        Cache<Integer> intCache = Cache.of(intSupplier);
        assertEquals(42, intCache.get());

        // Test with a String supplier
        Supplier<String> stringSupplier = () -> "Hello, Cache!";
        Cache<String> stringCache = Cache.of(stringSupplier);
        assertEquals("Hello, Cache!", stringCache.get());
    }

    @Test
    void testCacheHandlesMultipleInstancesIndependently() {
        // Two separate caches with independent suppliers
        AtomicInteger counter1 = new AtomicInteger(0);
        Supplier<Integer> supplier1 = counter1::incrementAndGet;

        AtomicInteger counter2 = new AtomicInteger(0);
        Supplier<Integer> supplier2 = counter2::incrementAndGet;

        Cache<Integer> cache1 = Cache.of(supplier1);
        Cache<Integer> cache2 = Cache.of(supplier2);

        // Interact with the first cache
        assertEquals(1, cache1.get());
        assertEquals(1, counter1.get());
        assertEquals(0, counter2.get());

        // Interact with the second cache
        assertEquals(1, cache2.get());
        assertEquals(1, counter2.get());
    }
}
