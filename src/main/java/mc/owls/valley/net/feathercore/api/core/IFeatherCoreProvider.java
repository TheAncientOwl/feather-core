package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.database.mongo.IDAOAccessor;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationManager;

public interface IFeatherCoreProvider extends IEconomyProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public IDAOAccessor getMongoDAO();

    public IPlayersDataManager getPlayersDataManager();

    public IConfigurationManager getConfigurationManager();

    public IPvPManager getPvPManager() throws ModuleNotEnabledException;

    public ITranslationManager getTranslationManager();
}
