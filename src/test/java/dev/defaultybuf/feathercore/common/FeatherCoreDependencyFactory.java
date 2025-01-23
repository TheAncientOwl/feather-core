/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DependencyInjector.java
 * @author Alexandru Delegeanu
 * @version 0.18
 * @description Create mocks / actual instances of all modules 
 *              and inject them into tests dependencies map
 */

package dev.defaultybuf.feathercore.common;

import dev.defaultybuf.feather.toolkit.testing.annotations.DependencyFactory;
import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherToolkitDependencyFactory.DependencyHelper;
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

public final class FeatherCoreDependencyFactory {

    @DependencyFactory(of = IPlayersData.class)
    public static final DependencyHelper<PlayersData> getPlayersDataFactory() {
        return new DependencyHelper<PlayersData>(
                PlayersData.class,
                IPlayersData.class,
                "PlayersData",
                "players-data");
    }

    @DependencyFactory(of = IFeatherEconomy.class)
    public static final DependencyHelper<FeatherEconomyProvider> getEconomyFactory() {
        return new DependencyHelper<FeatherEconomyProvider>(
                FeatherEconomyProvider.class,
                IFeatherEconomy.class,
                "FeatherEconomy",
                "economy");
    }

    @DependencyFactory(of = ILootChests.class)
    public static final DependencyHelper<LootChests> getLootChestsFactory() {
        return new DependencyHelper<LootChests>(
                LootChests.class,
                ILootChests.class,
                "LootChests",
                "lootchests");
    }

    @DependencyFactory(of = IMongoDB.class)
    public static final DependencyHelper<MongoManager> getMongoDBFactory() {
        return new DependencyHelper<MongoManager>(
                MongoManager.class,
                IMongoDB.class,
                "MongoManager",
                "mongodb");
    }

    @DependencyFactory(of = IPvPManager.class)
    public static final DependencyHelper<PvPManager> getPvPManagerFactory() {
        return new DependencyHelper<PvPManager>(
                PvPManager.class,
                IPvPManager.class,
                "PvPManager",
                "pvpmanager");
    }

    @DependencyFactory(of = ITeleport.class)
    public static final DependencyHelper<Teleport> getTeleportFactory() {
        return new DependencyHelper<Teleport>(
                Teleport.class,
                ITeleport.class,
                "Teleport",
                "teleport");
    }

}
