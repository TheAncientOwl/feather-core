package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IDAOAccessor;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.modules.loot.chests.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public interface IFeatherCoreProvider extends IEconomyProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public IDAOAccessor getMongoDAO();

    public IPlayersData getPlayersDataManager();

    public IConfigurationManager getConfigurationManager();

    public IPvPManager getPvPManager();

    public TranslationManager getTranslationManager();

    public ILootChestsModule getLootChestsModule();
}
