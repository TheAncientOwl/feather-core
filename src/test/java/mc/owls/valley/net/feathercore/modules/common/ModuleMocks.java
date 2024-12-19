/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModuleMocks.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Create mocks of all modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.modules.reload.components.ReloadModule;

public final class ModuleMocks {
    public static final String RELOAD_MODULE_NAME = "ReloadModule";

    public static ReloadModule ReloadModule() {
        var mockModule = mock(ReloadModule.class);

        var configMock = mock(IConfigFile.class);

        when(mockModule.getModuleName()).thenReturn(RELOAD_MODULE_NAME);
        when(mockModule.getConfig()).thenReturn(configMock);

        return mockModule;
    }
}
