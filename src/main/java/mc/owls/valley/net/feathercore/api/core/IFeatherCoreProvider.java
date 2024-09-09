package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.database.mongo.IDAOAccessor;

public interface IFeatherCoreProvider extends IEconomyProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public IDAOAccessor getMongoDAO();

    public IPlayersDataManager getPlayersDataManager();

    public IConfigurationManager getConfigurationManager();
}
