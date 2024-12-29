/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Utility class for developing unit tests that use modules
 */

package dev.defaultybuf.feathercore.modules.common;

import static org.mockito.Mockito.lenient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.defaultybuf.feathercore.api.core.IFeatherLogger;
import dev.defaultybuf.feathercore.core.interfaces.IEnabledModulesProvider;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

@ExtendWith(MockitoExtension.class)
public abstract class DependencyAccessorMocker {
    private List<AutoCloseable> injectedDependencies = null;

    protected static Map<Class<?>, Object> dependenciesMap;
    @Mock static protected JavaPlugin mockJavaPlugin;

    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;

    protected ILanguage mockLanguage;

    public static JavaPlugin getJavaPluginMock() {
        return mockJavaPlugin;
    }

    public static Map<Class<?>, Object> getDependenciesMap() {
        return dependenciesMap;
    }

    @BeforeEach
    void setUpTest() {
        lenient().when(mockJavaPlugin.getName()).thenReturn("FeatherCore");
        lenient().when(mockJavaPlugin.getServer()).thenReturn(mockServer);
        lenient().when(mockJavaPlugin.getDataFolder()).thenReturn(TestUtils.getTestDataFolder());

        dependenciesMap = new HashMap<>();

        dependenciesMap.put(JavaPlugin.class, mockJavaPlugin);
        dependenciesMap.put(IFeatherLogger.class, mockFeatherLogger);
        dependenciesMap.put(IEnabledModulesProvider.class, mockEnabledModulesProvider);

        mockLanguage = DependencyInjector.Language.Mock();

        injectedDependencies = injectDependencies();

        setUp();
    }

    @AfterEach
    void tearDownTest() {
        if (injectedDependencies != null) {
            for (final var module : injectedDependencies) {
                try {
                    module.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }

        tearDown();
    }

    protected List<AutoCloseable> injectDependencies() {
        return null;
    }

    protected void setUp() {}

    protected void tearDown() {}
}
