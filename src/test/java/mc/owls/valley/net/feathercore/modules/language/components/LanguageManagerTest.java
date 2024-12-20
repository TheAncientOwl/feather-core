/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageManagerTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit LanguageManager#0.8
 * @description Unit tests for LanguageManager
 */

package mc.owls.valley.net.feathercore.modules.language.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.modules.common.ModuleMocks;
import mc.owls.valley.net.feathercore.modules.common.ModuleTestMocker;
import mc.owls.valley.net.feathercore.modules.common.TestUtils;

class LanguageManagerTest extends ModuleTestMocker<LanguageManager> {
    private static final String EN_FILE_CONTENT = "test-key: 'test-string'";
    private static final Path EN_FILE_PATH = Paths.get("language", "en.yml");
    private static final String EN_KEY = "en";
    private static final String FR_KEY = "fr";

    @Mock Player mockPlayer;
    @Mock YamlConfiguration mockLanguageConfig;

    @Override
    protected Class<LanguageManager> getModuleClass() {
        return LanguageManager.class;
    }

    @Override
    protected String getModuleName() {
        return ModuleMocks.LANGUAGE_MODULE_NAME;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherDependencies() {
        return null;
    }

    @Test
    void testModuleBasics() {
        try (var enTempFile = TestUtils.makeTempFile(EN_FILE_PATH, EN_FILE_CONTENT)) {
            assertDoesNotThrow(() -> moduleInstance.onModuleEnable());

            assertDoesNotThrow(() -> {
                var enTranslation = moduleInstance.getTranslation(EN_KEY);

                assertNotNull(enTranslation, "Translation should not be null");
                assertEquals(enTranslation.getString("test-key"), "test-string");
            });

            assertDoesNotThrow(() -> moduleInstance.onModuleDisable());
        }
    }

    @Test
    void getTranslation_LanguageStringValidCache() {
        assertDoesNotThrow(() -> {
            try (var enTempFile = TestUtils.makeTempFile(EN_FILE_PATH, EN_FILE_CONTENT)) {
                // get translation first time to get loaded
                var enTranslation01 = moduleInstance.getTranslation(EN_KEY);

                assertNotNull(enTranslation01, "Translation should not be null");
                assertEquals(enTranslation01.getString("test-key"), "test-string");

                // get already loaded translation
                var enTranslation02 = moduleInstance.getTranslation(EN_KEY);

                assertNotNull(enTranslation02, "Translation should not be null");
                assertEquals(enTranslation02.getString("test-key"), "test-string");
            }
        });

    }

    @Test
    void getTranslation_LanguageStringNotValid() {
        assertDoesNotThrow(() -> {
            try (var enTempFile = TestUtils.makeTempFile(EN_FILE_PATH, EN_FILE_CONTENT)) {
                // get 'en' translation
                var enTranslation = moduleInstance.getTranslation(EN_KEY);

                assertNotNull(enTranslation, "'en' translation should not be null");
                assertEquals(enTranslation.getString("test-key"), "test-string");

                // get 'fr' translation
                var frTranslation = moduleInstance.getTranslation(FR_KEY);

                assertNotNull(frTranslation,
                        "'fr' translation should be the same as 'en' translation");
                assertEquals(frTranslation.getString("test-key"), "test-string");

            }
        });
    }

}
