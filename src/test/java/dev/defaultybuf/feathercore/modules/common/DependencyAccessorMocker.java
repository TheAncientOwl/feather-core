/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Utility class for developing unit tests that use modules
 */

package dev.defaultybuf.feathercore.modules.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.core.IFeatherLogger;
import dev.defaultybuf.feathercore.core.interfaces.IEnabledModulesProvider;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

@ExtendWith(MockitoExtension.class)
public abstract class DependencyAccessorMocker {
    protected Map<Class<?>, Object> dependenciesMap;

    protected ILanguage mockLanguage;
    @Mock protected JavaPlugin mockJavaPlugin;
    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;

    private List<AutoCloseable> actualModules = null;

    @BeforeEach
    void setUpDependencies() {
        mockLanguage = Modules.LANGUAGE.Mock();

        dependenciesMap = new HashMap<>();
        dependenciesMap.put(ILanguage.class, mockLanguage);
        dependenciesMap.put(JavaPlugin.class, mockJavaPlugin);
        dependenciesMap.put(IFeatherLogger.class, mockFeatherLogger);
        dependenciesMap.put(IEnabledModulesProvider.class, mockEnabledModulesProvider);

        final var otherMockDependencies = getOtherMockDependencies();
        if (otherMockDependencies != null) {
            for (final var dependency : otherMockDependencies) {
                dependenciesMap.put(dependency.first, dependency.second);
            }
        }

        actualModules = injectActualModules();

        Mockito.lenient().when(mockJavaPlugin.getName()).thenReturn("FeatherCore");
        Mockito.lenient().when(mockJavaPlugin.getServer()).thenReturn(mockServer);
        Mockito.lenient()
                .when(mockJavaPlugin.getDataFolder()).thenReturn(TestUtils.getTestDataFolder());
    }

    @AfterEach
    void tearDownActualModules() {
        if (actualModules != null) {
            for (final var module : actualModules) {
                try {
                    module.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: Refactor to void injectMockModules()
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        return null;
    }

    protected List<AutoCloseable> injectActualModules() {
        return null;
    }
}
