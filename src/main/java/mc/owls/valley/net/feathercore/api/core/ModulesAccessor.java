/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModulesAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Provide access to all modules based on their interface
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.HashMap;
import java.util.Map;

public class ModulesAccessor {
    private final Map<Class<?>, Object> modules;

    public static class ModulesMapBuilder {
        private final Map<Class<?>, Object> modules = new HashMap<>();

        public void addModule(final Class<?> clazz, final Object module) {
            this.modules.put(clazz, module);
        }

        public void removeModule(final Class<?> clazz) {
            this.modules.remove(clazz);
        }

        public Map<Class<?>, Object> getMap() {
            return this.modules;
        }
    }

    public ModulesAccessor(final Map<Class<?>, Object> modules) {
        this.modules = modules;
    }

    public <T> T getInterface(final Class<T> clazz) {
        return clazz.cast(modules.get(clazz));
    }
}
