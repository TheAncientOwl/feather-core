/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherCore.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Plugin entry point
 */

package mc.owls.valley.net.feathercore.core;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Cache;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.common.util.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.data.mongodb.components.MongoManager;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;
import mc.owls.valley.net.feathercore.modules.economy.components.FeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.loot.chests.components.LootChests;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.components.RestrictedPvP;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;
import net.milkbowl.vault.economy.Economy;

public class FeatherCore extends JavaPlugin implements IFeatherCoreProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private ModulesManager modulesManager = new ModulesManager();
    private IFeatherLogger featherLogger = null;

    private Cache<Teleport> teleport = null;
    private Cache<LootChests> lootChests = null;
    private Cache<MongoManager> mongoManager = null;
    private Cache<RestrictedPvP> restrictedPvP = null;
    private Cache<PlayersData> playersDataManager = null;
    private Cache<LanguageManager> languageManager = null;
    private Cache<FeatherEconomyProvider> economyProvider = null;

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();

        try {
            this.featherLogger = new FeatherLogger(this);
            this.modulesManager.onEnable(this);

            final var enableFinishTime = System.currentTimeMillis();

            getFeatherLogger().info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatElapsed(enableStartTime, enableFinishTime) + "&8)");
        } catch (final FeatherSetupException | ModuleNotEnabledException e) {
            this.featherLogger.error(StringUtils.exceptionToStr(e));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        this.modulesManager.onDisable(getFeatherLogger());
        getFeatherLogger().info("Goodbye&8!");
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public IFeatherLogger getFeatherLogger() {
        return this.featherLogger;
    }

    @Override
    public MongoManager getMongoDB() {
        return this.mongoManager.get();
    }

    @Override
    public PlayersData getPlayersData() {
        return this.playersDataManager.get();
    }

    @Override
    public RestrictedPvP getRestrictedPvP() {
        return this.restrictedPvP.get();
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.languageManager.get();
    }

    @Override
    public LootChests getLootChests() {
        return this.lootChests.get();
    }

    @Override
    public Economy getEconomy() {
        return this.economyProvider.get().getEconomy();
    }

    @Override
    public FeatherEconomyProvider getFeatherEconomy() {
        return this.economyProvider.get();
    }

    @Override
    public List<FeatherModule> getEnabledModules() {
        return this.modulesManager.getEnabledModules();
    }

    @Override
    public Teleport getTeleport() {
        return this.teleport.get();
    }

}
