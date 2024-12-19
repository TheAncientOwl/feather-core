/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Provide access to all dependencies based on their class
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.core.interfaces.IGeneralDependencyAccessor;
import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

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
        Object instance = getInterface(JavaPlugin.class);
        if (instance instanceof JavaPlugin) {
            return (JavaPlugin) instance;
        }
        throw new IllegalStateException("JavaPlugin instance not found in dependencies.");
    }

    @Override
    public IFeatherLogger getLogger() throws IllegalStateException {
        final var instance = getInterface(IFeatherLogger.class);
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("IFeatherLogger instance not found in dependencies.");
    }

    @Override
    public List<FeatherModule> getEnabledModules() throws IllegalStateException {
        final var instance = getInterface(IEnabledModulesProvider.class);
        if (instance != null) {
            return instance.getEnabledModules();
        }
        throw new IllegalStateException(
                "IEnabledModulesProvider instance not found in dependencies.");
    }

    @Override
    public ILanguage getLanguage() throws IllegalStateException {
        final var instance = getInterface(ILanguage.class);
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("ILanguage instance not found in dependencies.");
    }
}
