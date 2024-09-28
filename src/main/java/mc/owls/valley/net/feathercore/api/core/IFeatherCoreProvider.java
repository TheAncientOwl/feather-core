package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IEconomyProvider;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IDAOAccessor;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public interface IFeatherCoreProvider extends IEconomyProvider {
    public JavaPlugin getPlugin();

    public IFeatherLogger getFeatherLogger();

    public IDAOAccessor getMongoDAO();

    public IPlayersDataManager getPlayersDataManager();

    public IConfigurationManager getConfigurationManager();

    public IPvPManager getPvPManager();

    public TranslationManager getTranslationManager();

    public ILootChestsModule getLootChestsModule();
}
