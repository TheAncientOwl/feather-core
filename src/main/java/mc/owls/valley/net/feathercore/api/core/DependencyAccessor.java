/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Provide access to all dependencies based on their interface
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.HashMap;
import java.util.Map;

public class DependencyAccessor {
    private final Map<Class<?>, Object> dependencies;

    public static class DependencyMapBuilder {
        private final Map<Class<?>, Object> dependencies = new HashMap<>();

        public void addDependency(final Class<?> clazz, final Object dependency) {
            this.dependencies.put(clazz, dependency);
        }

        public void removeDependency(final Class<?> clazz) {
            this.dependencies.remove(clazz);
        }

        public Map<Class<?>, Object> getMap() {
            return this.dependencies;
        }
    }

    public DependencyAccessor(final Map<Class<?>, Object> dependencies) {
        this.dependencies = dependencies;
    }

    public <T> T getInterface(final Class<T> clazz) {
        return clazz.cast(dependencies.get(clazz));
    }
}
