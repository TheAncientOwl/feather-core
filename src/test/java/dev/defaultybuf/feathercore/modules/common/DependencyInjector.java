/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyInjector.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Create mocks / actual instances of all modules 
 *              and inject them into tests dependencies map
 */

package dev.defaultybuf.feathercore.modules.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import dev.defaultybuf.feathercore.api.configuration.IConfigFile;
import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.core.configuration.bukkit.BukkitConfigFile;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.data.players.components.PlayersData;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.components.FeatherEconomyProvider;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feathercore.modules.reload.components.ReloadModule;
import dev.defaultybuf.feathercore.modules.reload.interfaces.IReloadModule;

public final class DependencyInjector {
    public static enum Module {
        Language, Reload, PlayersData, Economy
    }

    @SuppressWarnings("unchecked")
    public static final <T extends FeatherModule> ModuleInjector<T> getInjector(Module module) {
        return (ModuleInjector<T>) moduleInjectors.get(module);
    }

    public static final ModuleInjector<LanguageManager> Language =
            new ModuleInjector<LanguageManager>(
                    LanguageManager.class,
                    ILanguage.class,
                    "LanguageManager",
                    "language");

    public static final ModuleInjector<ReloadModule> Reload =
            new ModuleInjector<ReloadModule>(
                    ReloadModule.class,
                    IReloadModule.class,
                    "ReloadModule",
                    "reload");

    public static final ModuleInjector<PlayersData> PlayersData =
            new ModuleInjector<PlayersData>(
                    PlayersData.class,
                    IPlayersData.class,
                    "PlayersData",
                    "players-data");

    public static final ModuleInjector<FeatherEconomyProvider> Economy =
            new ModuleInjector<FeatherEconomyProvider>(
                    FeatherEconomyProvider.class,
                    IFeatherEconomy.class,
                    "FeatherEconomy",
                    "economy");

    public static final record ModuleInjector<T extends FeatherModule>(Class<T> moduleClass,
            Class<?> interfaceClass,
            String name,
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
            var mockModule = mock(moduleClass);
            var mockConfig = mock(IConfigFile.class);

            lenient().when(mockModule.getModuleName()).thenReturn(name);
            lenient().when(mockModule.getConfig()).thenReturn(mockConfig);

            DependencyAccessorMocker.getDependenciesMap().put(interfaceClass, mockModule);

            return mockModule;
        }

        public TempModule<T> Actual(final Resource[] resources) {
            T moduleOut = null;

            var plugin = DependencyAccessorMocker.getJavaPluginMock();
            var dependencies = DependencyAccessorMocker.getDependenciesMap();

            lenient().when(plugin.getDataFolder())
                    .thenReturn(TestUtils.getTestDataFolder());

            final var resourcesTempFiles = new ArrayList<TempFile>();

            for (final var resource : resources) {
                resourcesTempFiles.add(makeTempResource(resource.path(), resource.content()));
            }

            try {
                moduleOut = (T) moduleClass.getConstructor(FeatherModule.InitData.class)
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

            dependencies.put(interfaceClass, tempModule.module());

            return tempModule;
        }
    }

    private static final Map<Module, ModuleInjector<?>> moduleInjectors = Map.of(
            Module.Language, Language,
            Module.Reload, Reload,
            Module.PlayersData, PlayersData,
            Module.Economy, Economy);

}
