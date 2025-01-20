/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherUnitTest.java
 * @author Alexandru Delegeanu
 * @version 0.16
 * @description Utility class for developing unit tests that use modules
 */

package dev.defaultybuf.feather.toolkit.testing.mockers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Field;
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

import dev.defaultybuf.feather.toolkit.api.interfaces.IEnabledModulesProvider;
import dev.defaultybuf.feather.toolkit.api.interfaces.IFeatherLogger;
import dev.defaultybuf.feather.toolkit.api.interfaces.IPlayerLanguageAccessor;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.testing.annotations.ActualModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.MockedModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.annotations.StaticMock;
import dev.defaultybuf.feather.toolkit.testing.mockers.DependencyInjector.Module;
import dev.defaultybuf.feather.toolkit.testing.utils.TestUtils;
import dev.defaultybuf.feather.toolkit.util.java.Pair;

@ExtendWith(MockitoExtension.class)
public abstract class FeatherUnitTest {
    protected static Map<Class<?>, Object> dependenciesMap;
    @Mock static protected JavaPlugin mockJavaPlugin;

    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;
    @Mock protected IPlayerLanguageAccessor mockPlayersLanguageAccessor;

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

        mockLanguage = DependencyInjector.Language.Mock();
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
}
