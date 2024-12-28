/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Utility class for developing unit tests that use modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

@ExtendWith(MockitoExtension.class)
public abstract class DependencyAccessorMocker {
    protected Map<Class<?>, Object> dependenciesMap;

    protected ILanguage mockLanguage;
    @Mock protected JavaPlugin mockJavaPlugin;
    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;

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

        Mockito.lenient().when(mockJavaPlugin.getName()).thenReturn("FeatherCore");
        Mockito.lenient().when(mockJavaPlugin.getServer()).thenReturn(mockServer);
        Mockito.lenient()
                .when(mockJavaPlugin.getDataFolder()).thenReturn(TestUtils.getTestDataFolder());
    }

    protected abstract List<Pair<Class<?>, Object>> getOtherMockDependencies();
}
