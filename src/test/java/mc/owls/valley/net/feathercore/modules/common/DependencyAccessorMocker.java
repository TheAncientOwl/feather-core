/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyAccessorMocker.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Utility class for developing unit tests that use modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;
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

    @Mock protected ILanguage mockLanguage;
    @Mock protected JavaPlugin mockJavaPlugin;
    @Mock protected IFeatherLogger mockFeatherLogger;
    @Mock protected IEnabledModulesProvider mockEnabledModulesProvider;
    @Mock protected Server mockServer;

    @BeforeEach
    void setUpDependencies() {
        dependenciesMap = new HashMap<>();
        dependenciesMap.put(ILanguage.class, mockLanguage);
        dependenciesMap.put(JavaPlugin.class, mockJavaPlugin);
        dependenciesMap.put(IFeatherLogger.class, mockFeatherLogger);
        dependenciesMap.put(IEnabledModulesProvider.class, mockEnabledModulesProvider);

        final var otherModules = getOtherDependencies();
        if (otherModules != null) {
            for (final var dependency : otherModules) {
                dependenciesMap.put(dependency.first, dependency.second);
            }
        }

        Mockito.lenient().when(mockJavaPlugin.getServer()).thenReturn(mockServer);
        Mockito.lenient()
                .when(mockJavaPlugin.getDataFolder()).thenReturn(TestUtils.getTestDataFolder());
    }

    protected void addMock(Class<?> clazz) {
        dependenciesMap.put(clazz, mock(clazz));
    }

    protected abstract List<Pair<Class<?>, Object>> getOtherDependencies();
}
