/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModuleTestMocker.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class for developing module unit tests that use modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;

public abstract class ModuleTestMocker<ModuleType extends FeatherModule>
        extends DependencyAccessorMocker {
    @Mock protected IConfigFile mockConfig;
    protected ModuleType moduleInstance;

    @BeforeEach
    void setUpModuleTest()
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        moduleInstance = getModuleClass().getConstructor(FeatherModule.InitData.class).newInstance(
                new FeatherModule.InitData(getModuleName(), () -> mockConfig, dependenciesMap));

        dependenciesMap.put(getModuleClass(), moduleInstance);
    }

    protected abstract Class<ModuleType> getModuleClass();

    protected abstract String getModuleName();
}
