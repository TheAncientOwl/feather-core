package mc.owls.valley.net.feathercore.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.Cache;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IDAOAccessor;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.modules.loot.chests.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.milkbowl.vault.economy.Economy;

public class FeatherCore extends JavaPlugin implements IFeatherCoreProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private ModulesManager modulesManager = new ModulesManager();
    private IFeatherLogger featherLogger = null;

    private Cache<IPvPManager> pvpManager = null;
    private Cache<IDAOAccessor> mongoManager = null;
    private Cache<IEconomyProvider> economyProvider = null;
    private Cache<ILootChestsModule> lootChests = null;
    private Cache<TranslationManager> translationManager = null;
    private Cache<IPlayersData> playersDataManager = null;
    private Cache<IConfigurationManager> configurationManager = null;

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
    public IDAOAccessor getMongoDAO() {
        return this.mongoManager.get();
    }

    @Override
    public IPlayersData getPlayersDataManager() {
        return this.playersDataManager.get();
    }

    @Override
    public IConfigurationManager getConfigurationManager() {
        return this.configurationManager.get();
    }

    @Override
    public Economy getEconomy() {
        return this.economyProvider.get().getEconomy();
    }

    @Override
    public IPvPManager getPvPManager() {
        return this.pvpManager.get();
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.translationManager.get();
    }

    @Override
    public ILootChestsModule getLootChestsModule() {
        return this.lootChests.get();
    }

}
