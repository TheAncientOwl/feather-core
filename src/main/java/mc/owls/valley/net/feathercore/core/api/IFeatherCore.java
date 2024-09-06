package mc.owls.valley.net.feathercore.core.api;

import mc.owls.valley.net.feathercore.logging.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.modules.configuration.manager.api.IConfigurationManager;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IMongoManager;
import mc.owls.valley.net.feathercore.modules.data.players.manager.api.IPlayersDataManager;

public interface IFeatherCore {

    public IFeatherLoggger getFeatherLogger();

    public IMongoManager getMongoManager();

    public IPlayersDataManager getPlayersDataManager();

    public IConfigurationManager getConfigurationManager();
}
