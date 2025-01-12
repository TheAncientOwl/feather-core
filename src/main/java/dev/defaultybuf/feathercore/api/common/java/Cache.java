/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Cache.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Utility class for caching result of a given supplier
 */

package dev.defaultybuf.feathercore.api.common.java;

import java.util.function.Supplier;

public class Cache<T> {
    private T obj = null;
    private final Supplier<T> supplier;

    public static <T> Cache<T> of(final Supplier<T> supplier) {
        return new Cache<T>(supplier);
    }

    public Cache(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.obj == null) {
            this.obj = this.supplier.get();
        }
        return obj;
    }
}
