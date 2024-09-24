package mc.owls.valley.net.feathercore.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.Cache;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.database.mongo.IDAOAccessor;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.log.components.FeatherLogger;
import mc.owls.valley.net.feathercore.modules.logo.components.LogoManager;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.milkbowl.vault.economy.Economy;

public class FeatherCore extends JavaPlugin implements IFeatherCoreProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private ModulesManager modulesManager = new ModulesManager();
    private IFeatherLogger featherLogger = null;

    @SuppressWarnings("unused")
    private Cache<LogoManager> logoManager = null;
    private Cache<IPvPManager> pvpManager = null;
    private Cache<IDAOAccessor> mongoManager = null;
    private Cache<IEconomyProvider> economyProvider = null;
    private Cache<ILootChestsModule> lootChests = null;
    private Cache<TranslationManager> translationManager = null;
    private Cache<IPlayersDataManager> playersDataManager = null;
    private Cache<IConfigurationManager> configurationManager = null;

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();
        this.featherLogger = new FeatherLogger(this);

        try {
            this.modulesManager.onEnable(this);

            final var enableFinishTime = System.currentTimeMillis();
            getFeatherLogger().info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatElapsed(enableStartTime, enableFinishTime) + "&8)");
        } catch (final FeatherSetupException e) {
            getFeatherLogger().error(StringUtils.exceptionToStr(e));

            getServer().getPluginManager().disablePlugin(this);
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
    public IPlayersDataManager getPlayersDataManager() {
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
    public IPvPManager getPvPManager() throws ModuleNotEnabledException {
        final var module = this.pvpManager.get();

        if (module instanceof FeatherModule) {
            final var moduleName = ((FeatherModule) module).getModuleName();

            if (!this.modulesManager.isModuleEnabled(moduleName)) {
                throw new ModuleNotEnabledException(moduleName);
            }
        } else {
            getFeatherLogger()
                    .error("Implementation error on FeatherCore.getPvPManager. Please ping the developer o.O");
        }

        return module;
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
