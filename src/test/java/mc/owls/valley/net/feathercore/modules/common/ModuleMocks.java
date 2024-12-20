/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModuleMocks.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Create mocks of all modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;
import org.mockito.Mockito;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.reload.components.ReloadModule;

public final class ModuleMocks {
    public static final String RELOAD_MODULE_NAME = "ReloadModule";
    public static final String LANGUAGE_MODULE_NAME = "LanguageManager";

    public static ReloadModule ReloadModule() {
        return makeModule(ReloadModule.class, RELOAD_MODULE_NAME);
    }

    public static LanguageManager LanguageManager() {
        return makeModule(LanguageManager.class, LANGUAGE_MODULE_NAME);
    }

    private static <T extends FeatherModule> T makeModule(final Class<T> clazz, final String name) {
        var mockModule = mock(clazz);

        var configMock = mock(IConfigFile.class);

        Mockito.lenient().when(mockModule.getModuleName()).thenReturn(name);
        Mockito.lenient().when(mockModule.getConfig()).thenReturn(configMock);

        return mockModule;
    }
}
