/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherCoreProvider.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Interface that provides access to plugin modules
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.modules.data.mongodb.components.MongoManager;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;
import mc.owls.valley.net.feathercore.modules.economy.components.FeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.loot.chests.components.LootChests;
import mc.owls.valley.net.feathercore.modules.pvp.manager.components.PvPManager;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;
import net.milkbowl.vault.economy.Economy;

public interface IFeatherCoreProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public MongoManager getMongoDB();

    public PlayersData getPlayersData();

    public PvPManager getPvPManager();

    public LanguageManager getLanguageManager();

    public LootChests getLootChests();

    public FeatherEconomyProvider getFeatherEconomy();

    public Teleport getTeleport();

    public List<FeatherModule> getEnabledModules();

    public Economy getEconomy();
}
