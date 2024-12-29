/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Modules.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Create mocks / actual instances of all modules
 */

package dev.defaultybuf.feathercore.modules.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.plugin.java.JavaPlugin;
import org.mockito.Mockito;

import dev.defaultybuf.feathercore.api.configuration.IConfigFile;
import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.core.configuration.bukkit.BukkitConfigFile;
import dev.defaultybuf.feathercore.modules.data.players.components.PlayersData;
import dev.defaultybuf.feathercore.modules.economy.components.FeatherEconomyProvider;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.reload.components.ReloadModule;

public final class Modules {
    public static final <T> Class<T> injectAs(Class<T> clazz) {
        return clazz;
    }

    public static final List<Resource> withResources(Resource... resources) {
        return List.of(resources);
    }

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

        public TempModule<T> Actual(JavaPlugin plugin, Map<Class<?>, Object> dependencies,
                Class<?> injectAs, List<Resource> resources) {
            T moduleOut = null;

            Mockito.lenient().when(plugin.getDataFolder())
                    .thenReturn(TestUtils.getTestDataFolder());

            final var resourcesTempFiles = new ArrayList<TempFile>();

            for (final var resource : resources) {
                resourcesTempFiles.add(makeTempResource(resource.path(), resource.content()));
            }

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

            TempModule<T> tempModule = new TempModule<T>(moduleOut, resourcesTempFiles);

            assertNotNull(tempModule.module().getConfig(),
                    "Failed to load config file for " + name + " module, '" + relativeConfig()
                            + "'");

            dependencies.put(injectAs, tempModule.module());

            return tempModule;
        }
    }

}
