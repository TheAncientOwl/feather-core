/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Provide access to all dependencies based on their class
 */

package dev.defaultybuf.feathercore.api.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import dev.defaultybuf.feathercore.api.core.interfaces.IGeneralDependencyAccessor;
import dev.defaultybuf.feathercore.core.interfaces.IEnabledModulesProvider;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

public class DependencyAccessor implements IGeneralDependencyAccessor {
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

    @Override
    public JavaPlugin getPlugin() throws IllegalStateException {
        assert getInterface(
                JavaPlugin.class) != null : "JavaPlugin instance not found in dependencies.";

        return getInterface(JavaPlugin.class);
    }

    @Override
    public IFeatherLogger getLogger() throws IllegalStateException {
        assert getInterface(
                IFeatherLogger.class) != null : "IFeatherLogger instance not found in dependencies.";

        return getInterface(IFeatherLogger.class);
    }

    @Override
    public List<FeatherModule> getEnabledModules() throws IllegalStateException {
        final var instance = getInterface(IEnabledModulesProvider.class);

        assert instance != null : "IEnabledModulesProvider instance not found in dependencies.";

        return instance.getEnabledModules();
    }

    @Override
    public ILanguage getLanguage() throws IllegalStateException {
        assert getInterface(
                ILanguage.class) != null : "ILanguage instance not found in dependencies.";

        return getInterface(ILanguage.class);
    }
}
