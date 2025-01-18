/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyInjector.java
 * @author Alexandru Delegeanu
 * @version 0.15
 * @description Create mocks / actual instances of all modules 
 *              and inject them into tests dependencies map
 */

package dev.defaultybuf.feather.toolkit.testing.mockers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.api.configuration.IConfigFile;
import dev.defaultybuf.feather.toolkit.core.configuration.bukkit.BukkitConfigFile;
import dev.defaultybuf.feather.toolkit.core.modules.language.components.LanguageManager;
import dev.defaultybuf.feather.toolkit.core.modules.language.interfaces.ILanguage;
import dev.defaultybuf.feather.toolkit.core.modules.reload.components.ReloadModule;
import dev.defaultybuf.feather.toolkit.core.modules.reload.interfaces.IReloadModule;
import dev.defaultybuf.feather.toolkit.testing.annotations.Resource;
import dev.defaultybuf.feather.toolkit.testing.utils.TempFile;
import dev.defaultybuf.feather.toolkit.testing.utils.TempModule;
import dev.defaultybuf.feather.toolkit.testing.utils.TestUtils;
import dev.defaultybuf.feathercore.modules.data.mongodb.components.MongoManager;
import dev.defaultybuf.feathercore.modules.data.mongodb.interfaces.IMongoDB;
import dev.defaultybuf.feathercore.modules.data.players.components.PlayersData;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.components.FeatherEconomyProvider;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.loot.chests.components.LootChests;
import dev.defaultybuf.feathercore.modules.loot.chests.interfaces.ILootChests;
import dev.defaultybuf.feathercore.modules.pvp.manager.components.PvPManager;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public final class DependencyInjector {
    public static enum Module {
        Language, Reload, PlayersData, Economy, LootChests, MongoDB, PvPManager, Teleport
    }

    @SuppressWarnings("unchecked")
    public static final <T extends FeatherModule> ModuleInjector<T> getInjector(Module module) {
        assert moduleInjectors.containsKey(
                module) : "[modules.common.mockers]@DependencyInjector.getInjector(Module): Module injector not found for "
                        + module;

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

    public static final ModuleInjector<LootChests> LootChests =
            new ModuleInjector<LootChests>(
                    LootChests.class,
                    ILootChests.class,
                    "LootChests",
                    "lootchests");

    public static final ModuleInjector<MongoManager> MongoDB =
            new ModuleInjector<MongoManager>(
                    MongoManager.class,
                    IMongoDB.class,
                    "MongoManager",
                    "mongodb");

    public static final ModuleInjector<PvPManager> PvPManager =
            new ModuleInjector<PvPManager>(
                    PvPManager.class,
                    IPvPManager.class,
                    "PvPManager",
                    "pvpmanager");

    public static final ModuleInjector<Teleport> Teleport =
            new ModuleInjector<Teleport>(
                    Teleport.class,
                    ITeleport.class,
                    "Teleport",
                    "teleport");

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

            FeatherUnitTest.getDependenciesMap().put(interfaceClass, mockModule);

            return mockModule;
        }

        public TempModule<T> Actual(final Resource[] resources) {
            T moduleOut = null;

            var plugin = FeatherUnitTest.getJavaPluginMock();
            var dependencies = FeatherUnitTest.getDependenciesMap();

            lenient().when(plugin.getDataFolder())
                    .thenReturn(TestUtils.getTestDataFolder());

            final var resourcesTempFiles = new ArrayList<TempFile>();

            for (final var resource : resources) {
                resourcesTempFiles.add(makeTempResource(resource.path(),
                        resource.content()));
            }

            try {
                moduleOut = (T) moduleClass
                        .getConstructor(FeatherModule.InitData.class)
                        .newInstance(new FeatherModule.InitData(
                                name,
                                (Supplier<IConfigFile>) () -> {
                                    try {
                                        return relativeConfig() == null
                                                ? null
                                                : new BukkitConfigFile(
                                                        plugin,
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
                    "Failed to load config file for " + name + " module, '"
                            + relativeConfig()
                            + "'");

            dependencies.put(interfaceClass, tempModule.module());

            return tempModule;
        }
    }

    static final Map<Module, ModuleInjector<?>> moduleInjectors = Map.of(
            Module.Language, Language,
            Module.Reload, Reload,
            Module.PlayersData, PlayersData,
            Module.Economy, Economy,
            Module.LootChests, LootChests,
            Module.MongoDB, MongoDB,
            Module.PvPManager, PvPManager,
            Module.Teleport, Teleport);

}
