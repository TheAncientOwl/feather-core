/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Modules.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Create mocks / actual instances of all modules
 */

package mc.owls.valley.net.feathercore.modules.common;

import static org.mockito.Mockito.mock;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockito.Mockito;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.core.configuration.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;
import mc.owls.valley.net.feathercore.modules.economy.components.FeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.reload.components.ReloadModule;

public final class Modules {
    public static final ModuleConfig<LanguageManager> LANGUAGE =
            new ModuleConfig<LanguageManager>(
                    LanguageManager.class,
                    "LanguageManager",
                    "language");

    public static final ModuleConfig<ReloadModule> RELOAD =
            new ModuleConfig<ReloadModule>(
                    ReloadModule.class,
                    "ReloadModule",
                    "reload");

    public static final ModuleConfig<PlayersData> PLAYERS_DATA =
            new ModuleConfig<PlayersData>(
                    PlayersData.class,
                    "PlayersData",
                    "players-data");

    public static final ModuleConfig<FeatherEconomyProvider> ECONOMY =
            new ModuleConfig<FeatherEconomyProvider>(
                    FeatherEconomyProvider.class,
                    "FeatherEconomy",
                    "economy");

    public static final record ModuleConfig<T extends FeatherModule>(Class<T> clazz, String name,
            String relativeFolder) {
        public Path absoluteConfig() {
            return absoluteResource("config.yml");
        }

        public Path relativeConfig() {
            return relativeResource("config.yml");
        }

        public Path relativeResource(Object resource) {
            return Paths.get(relativeFolder, resource.toString());
        }

        public Path absoluteResource(Object resource) {
            return TestUtils.getTestDataFolderPath()
                    .resolve(Paths.get(relativeFolder, resource.toString()));
        }

        public TempFile makeTempConfig(String content) {
            return TestUtils.makeTempFile(absoluteConfig(), content);
        }

        public TempFile makeTempResource(Object relativePath, String content) {
            return TestUtils.makeTempFile(absoluteResource(relativePath), content);
        }

        public T Mock() {
            var mockModule = mock(clazz);
            var configMock = mock(IConfigFile.class);

            Mockito.lenient().when(mockModule.getModuleName()).thenReturn(name);
            Mockito.lenient().when(mockModule.getConfig()).thenReturn(configMock);

            return mockModule;
        }

        public T Actual(JavaPlugin plugin, Map<Class<?>, Object> dependencies) {
            T moduleOut = null;

            try {
                moduleOut = (T) clazz.getConstructor(FeatherModule.InitData.class)
                        .newInstance(new FeatherModule.InitData(
                                name,
                                (Supplier<IConfigFile>) () -> {
                                    try {
                                        return relativeConfig() == null ? null
                                                : new BukkitConfigFile(plugin,
                                                        relativeConfig().toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }, dependencies));

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            return moduleOut;
        }
    }

}
