/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.12
 * @description Utility class for developing unit tests that use modules
 */

package dev.defaultybuf.feathercore.modules.common.mockers;

import static org.mockito.Mockito.lenient;

import java.lang.reflect.Field;
import java.util.HashMap;
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
import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.utils.TestUtils;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

@ExtendWith(MockitoExtension.class)
public abstract class DependencyAccessorMocker {
    protected static Map<Class<?>, Object> dependenciesMap;
    @Mock static protected JavaPlugin mockJavaPlugin;

    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;

    @MockedModule(of = Module.Language) protected ILanguage mockLanguage;

    public static JavaPlugin getJavaPluginMock() {
        return mockJavaPlugin;
    }

    public static Map<Class<?>, Object> getDependenciesMap() {
        return dependenciesMap;
    }

    protected void setUp() {}

    protected void tearDown() {}

    @BeforeEach
    void setUpTest() {
        lenientCommons();
        initDependencies();
        setUp();
    }

    @AfterEach
    void tearDownTest() {
        closeResources();
        tearDown();
    }

    void closeResources() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ActualModule.class)) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(this);
                    if (fieldValue instanceof AutoCloseable) {
                        ((AutoCloseable) fieldValue).close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void lenientCommons() {
        lenient().when(mockJavaPlugin.getName()).thenReturn("FeatherCore");
        lenient().when(mockJavaPlugin.getServer()).thenReturn(mockServer);
        lenient().when(mockJavaPlugin.getDataFolder()).thenReturn(TestUtils.getTestDataFolder());
    }

    void initDependencies() {
        dependenciesMap = new HashMap<>();

        dependenciesMap.put(JavaPlugin.class, mockJavaPlugin);
        dependenciesMap.put(IFeatherLogger.class, mockFeatherLogger);
        dependenciesMap.put(IEnabledModulesProvider.class, mockEnabledModulesProvider);

        mockLanguage = DependencyInjector.Language.Mock();

        injectModules();
    }

    void injectModules() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MockedModule.class)) {
                injectMockedModule(field);
            } else if (field.isAnnotationPresent(ActualModule.class)) {
                injectActualModule(field);
            }
        }
    }

    void injectMockedModule(final Field field) {
        field.setAccessible(true);
        try {
            MockedModule annotation = field.getAnnotation(MockedModule.class);
            field.set(this, DependencyInjector.getInjector(annotation.of()).Mock());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void injectActualModule(final Field field) {
        field.setAccessible(true);
        try {
            ActualModule annotation = field.getAnnotation(ActualModule.class);
            Resource[] resources = annotation.resources();

            field.set(this,
                    DependencyInjector.getInjector(annotation.of()).Actual(resources));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
