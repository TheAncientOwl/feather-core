package mc.owls.valley.net.feathercore.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.database.mongo.IDAOAccessor;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.economy.components.FeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.milkbowl.vault.economy.Economy;

public class FeatherCore extends JavaPlugin implements IFeatherCoreProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private static String LITERAL_FEATHER_LOGGER = null;
    private static String LITERAL_MONGO_MANAGER = null;
    private static String LITERAL_PLAYERS_DATA_MANAGER = null;
    private static String LITERAL_CONFIG_MANAGER = null;
    private static String LITERAL_ECONOMY_PROVIDER = null;
    private static String LITERAL_PVP_MANAGER = null;
    private static String LITERAL_TRANSLATION_MANAGER = null;
    private static String LITERAL_LOOT_CHESTS_MANAGER = null;

    private ModulesManager modulesManager = new ModulesManager();

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();

        LogoManager.logLogo(getServer());

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
        LogoManager.logLogo(getServer());

        this.modulesManager.onDisable(getFeatherLogger());

        getFeatherLogger().info("Goodbye&8!");
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public IFeatherLogger getFeatherLogger() {
        return this.modulesManager.getModule(LITERAL_FEATHER_LOGGER);
    }

    @Override
    public IDAOAccessor getMongoDAO() {
        return this.modulesManager.getModule(LITERAL_MONGO_MANAGER);
    }

    @Override
    public IPlayersDataManager getPlayersDataManager() {
        return this.modulesManager.getModule(LITERAL_PLAYERS_DATA_MANAGER);
    }

    @Override
    public IConfigurationManager getConfigurationManager() {
        return this.modulesManager.getModule(LITERAL_CONFIG_MANAGER);
    }

    @Override
    public Economy getEconomy() {
        final FeatherEconomyProvider economyModule = this.modulesManager.getModule(LITERAL_ECONOMY_PROVIDER);
        return economyModule.getEconomy();
    }

    @Override
    public IPvPManager getPvPManager() throws ModuleNotEnabledException {
        if (!this.modulesManager.isModuleEnabled(LITERAL_PVP_MANAGER)) {
            throw new ModuleNotEnabledException(LITERAL_PVP_MANAGER);
        }

        return this.modulesManager.getModule(LITERAL_PVP_MANAGER);
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.modulesManager.getModule(LITERAL_TRANSLATION_MANAGER);
    }

    @Override
    public ILootChestsModule getLootChestsModule() {
        return this.modulesManager.getModule(LITERAL_LOOT_CHESTS_MANAGER);
    }

}
