/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit DependencyAccessor#0.2
 * @description Unit tests for DependencyAccessor
 */

package mc.owls.valley.net.feathercore.api.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

@ExtendWith(MockitoExtension.class)
class DependencyAccessorTest {
    DependencyAccessor dependencyAccessor;
    Map<Class<?>, Object> dependencyMap;

    @Mock JavaPlugin mockPlugin;
    @Mock IFeatherLogger mockLogger;
    @Mock IEnabledModulesProvider mockModulesProvider;
    @Mock ILanguage mockLanguage;

    @BeforeEach
    void setUp() {
        dependencyMap = new HashMap<>();
        dependencyMap.put(JavaPlugin.class, mockPlugin);
        dependencyMap.put(IFeatherLogger.class, mockLogger);
        dependencyMap.put(IEnabledModulesProvider.class, mockModulesProvider);
        dependencyMap.put(ILanguage.class, mockLanguage);

        dependencyAccessor = new DependencyAccessor(dependencyMap);
    }

    @Test
    void getInterface_validDependency() {
        JavaPlugin plugin = dependencyAccessor.getInterface(JavaPlugin.class);
        assertNotNull(plugin);
        assertEquals(mockPlugin, plugin);
    }

    @Test
    void getInterface_missingDependency() {
        assertNull(dependencyAccessor.getInterface(String.class));
    }

    @Test
    void getPlugin_success() {
        JavaPlugin plugin = dependencyAccessor.getPlugin();
        assertNotNull(plugin);
        assertEquals(mockPlugin, plugin);
    }

    @Test
    void getPlugin_missingDependency() {
        dependencyMap.remove(JavaPlugin.class);
        assertThrows(IllegalStateException.class, () -> dependencyAccessor.getPlugin());
    }

    @Test
    void getLogger_success() {
        IFeatherLogger logger = dependencyAccessor.getLogger();
        assertNotNull(logger);
        assertEquals(mockLogger, logger);
    }

    @Test
    void getLogger_missingDependency() {
        dependencyMap.remove(IFeatherLogger.class);
        assertThrows(IllegalStateException.class, () -> dependencyAccessor.getLogger());
    }

    @Test
    void getEnabledModules_success() {
        List<FeatherModule> mockModules = List.of(mock(FeatherModule.class));
        Mockito.when(mockModulesProvider.getEnabledModules()).thenReturn(mockModules);

        List<FeatherModule> modules = dependencyAccessor.getEnabledModules();
        assertNotNull(modules);
        assertEquals(mockModules, modules);
    }

    @Test
    void getEnabledModules_missingDependency() {
        dependencyMap.remove(IEnabledModulesProvider.class);
        assertThrows(IllegalStateException.class, () -> dependencyAccessor.getEnabledModules());
    }

    @Test
    void getLanguage_success() {
        ILanguage language = dependencyAccessor.getLanguage();
        assertNotNull(language);
        assertEquals(mockLanguage, language);
    }

    @Test
    void getLanguage_missingDependency() {
        dependencyMap.remove(ILanguage.class);
        assertThrows(IllegalStateException.class, () -> dependencyAccessor.getLanguage());
    }
}
