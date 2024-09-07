package mc.owls.valley.net.feathercore.api;

import mc.owls.valley.net.feathercore.api.data.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.data.mongo.IMongoDB;

public interface IFeatherCore {

    public IFeatherLoggger getFeatherLogger();

    public IMongoDB getMongoDB();

    public IPlayersDataManager getPlayersDataManager();

    public IFeatherConfigurationManager getConfigurationManager();
}
