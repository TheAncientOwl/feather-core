/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherCoreProvider.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Interface that provides access to plugin modules
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.modules.data.mongodb.components.MongoManager;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;
import mc.owls.valley.net.feathercore.modules.economy.components.FeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.modules.loot.chests.components.LootChests;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.components.RestrictedPvP;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public interface IFeatherCoreProvider extends IEconomyProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public MongoManager getMongoDB();

    public PlayersData getPlayersData();

    public RestrictedPvP getRestrictedPvP();

    public TranslationManager getTranslationManager();

    public LootChests getLootChests();

    public FeatherEconomyProvider getFeatherEconomy();

    public Teleport getTeleport();

    public List<FeatherModule> getEnabledModules();
}
