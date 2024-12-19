/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class for developing unit tests that use modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;

import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

public abstract class DependencyAccessorMocker {
    protected Map<Class<?>, Object> dependenciesMap = null;

    protected ILanguage mockLanguage = null;
    protected JavaPlugin mockJavaPlugin = null;
    protected IFeatherLogger mockFeatherLogger = null;
    protected IEnabledModulesProvider mockEnabledModulesProvider = null;

    @BeforeEach
    void setUpDependencies() {
        dependenciesMap = new HashMap<>();

        mockLanguage = mock(ILanguage.class);
        mockJavaPlugin = mock(JavaPlugin.class);
        mockFeatherLogger = mock(IFeatherLogger.class);
        mockEnabledModulesProvider = mock(IEnabledModulesProvider.class);

        dependenciesMap.put(ILanguage.class, mockLanguage);
        dependenciesMap.put(JavaPlugin.class, mockJavaPlugin);
        dependenciesMap.put(IFeatherLogger.class, mockFeatherLogger);
        dependenciesMap.put(IEnabledModulesProvider.class, mockEnabledModulesProvider);
    }

    protected void addMock(Class<?> clazz) {
        dependenciesMap.put(clazz, mock(clazz));
    }
}
