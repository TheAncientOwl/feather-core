/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherUnitTest.java
 * @author Alexandru Delegeanu
 * @version 0.18
 * @description Utility class for developing unit tests that use modules
 */

package dev.defaultybuf.feather.toolkit.testing.mockers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.api.interfaces.IEnabledModulesProvider;
import dev.defaultybuf.feather.toolkit.api.interfaces.IFeatherLogger;
import dev.defaultybuf.feather.toolkit.api.interfaces.IPlayerLanguageAccessor;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.DependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.annotations.InjectDependencies;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.annotations.StaticMock;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherToolkitDependencyFactory.DependencyHelper;
import dev.defaultybuf.feather.toolkit.testing.utils.TestUtils;
import dev.defaultybuf.feather.toolkit.util.java.Pair;

@ExtendWith(MockitoExtension.class)
public abstract class FeatherUnitTest {
    @Mock protected Server mockServer;
    @Mock protected JavaPlugin mockJavaPlugin;
    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected IPlayerLanguageAccessor mockPlayersLanguageAccessor;

    @MockedModule protected ILanguage mockLanguage;

    protected Map<Class<?>, Object> dependenciesMap;

    protected void setUp() {}

    protected void tearDown() {}

    @BeforeEach
    void setUpDependencyAccessorTest() {
        lenientCommons();
        initDependencies();
        setUpAnnotations();
        setUp();
    }

    @AfterEach
    void tearDownDependencyAccessorTest() {
        closeResources();
        tearDown();
    }

    void closeResources() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ActualModule.class)
                    || field.isAnnotationPresent(StaticMock.class)) {
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
        dependenciesMap.put(IPlayerLanguageAccessor.class, mockPlayersLanguageAccessor);

        lenient().when(mockPlayersLanguageAccessor.getPlayerLanguageCode(any(OfflinePlayer.class)))
                .thenReturn("en");

        mockLanguage =
                FeatherToolkitDependencyFactory.getLanguageFactory().MockModule(dependenciesMap);
        dependenciesMap.put(ILanguage.class, mockLanguage);
    }

    void setUpAnnotations() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MockedModule.class)) {
                injectMockedModule(field);
            } else if (field.isAnnotationPresent(ActualModule.class)) {
                injectActualModule(field);
            } else if (field.isAnnotationPresent(StaticMock.class)) {
                createStaticMock(field);
            }
        }
    }

    void injectMockedModule(final Field field) {
        field.setAccessible(true);
        try {
            field.set(this, getDependencyHelperOf(field.getType()).MockModule(dependenciesMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void injectActualModule(final Field field) {
        field.setAccessible(true);
        try {
            ActualModule annotation = field.getAnnotation(ActualModule.class);
            Resource[] resources = annotation.resources();

            field.set(this, getDependencyHelperOf(annotation.of()).ActualModule(resources,
                    mockJavaPlugin, dependenciesMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createStaticMock(final Field field) {
        field.setAccessible(true);
        try {
            field.set(this, mockStatic(field.getAnnotation(StaticMock.class).of()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected Pair<String, Object> anyPlaceholder() {
        return any(Pair.class);
    }

    @SuppressWarnings("unchecked")
    <T extends FeatherModule> DependencyHelper<T> getDependencyHelperOf(
            final Class<?> interfaceClass) {
        for (final var factoryClass : getDependencyFactoryClasses()) {
            final var dependencyHelper = getDependencyHelperOf(interfaceClass, factoryClass);
            if (dependencyHelper != null) {
                return (DependencyHelper<T>) dependencyHelper;
            }
        }

        assert false : "Missing dependency factory of interface " + interfaceClass.getName()
                + " in test class " + this.getClass().getName();
        return null;
    }

    @SuppressWarnings("unchecked")
    <T extends FeatherModule> DependencyHelper<T> getDependencyHelperOf(
            final Class<?> interfaceClass, final Class<?> factoryClass) {
        for (final var method : factoryClass.getMethods()) {
            if (method.isAnnotationPresent(DependencyFactory.class)
                    && method.getAnnotation(DependencyFactory.class).of().equals(interfaceClass)) {
                try {
                    return (DependencyHelper<T>) method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    assert false : "Failed to invoke dependency factory of interface "
                            + interfaceClass.getName()
                            + ". It should be static and return a DependencyHelper";
                }
            }
        }
        return null;
    }

    Class<?>[] getDependencyFactoryClasses() {
        assert this.getClass().isAnnotationPresent(
                InjectDependencies.class) : "Missing InjectDependencies annotation for test class "
                        + this.getClass().getName();

        final var annotation = this.getClass().getAnnotation(InjectDependencies.class);
        final var factories = annotation.factories();

        final var result = new Class<?>[factories.length + 1];
        for (int i = 0; i < factories.length; ++i) {
            result[i] = factories[i];
        }
        result[factories.length] = FeatherToolkitDependencyFactory.class;

        return result;
    }
}
