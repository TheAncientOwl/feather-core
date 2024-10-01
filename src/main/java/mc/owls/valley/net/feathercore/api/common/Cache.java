/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Cache.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class for caching result of a given supplier
 */

package mc.owls.valley.net.feathercore.api.common;

import java.util.Optional;
import java.util.function.Supplier;

public class Cache<T> {
    private final Optional<T> obj = Optional.empty();
    private final Supplier<T> supplier;

    public static <T> Cache<T> of(final Supplier<T> supplier) {
        return new Cache<T>(supplier);
    }

    public Cache(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        return this.obj.orElseGet(this.supplier);
    }
}
