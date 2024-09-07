package mc.owls.valley.net.feathercore.api;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface IFeatherConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();
}
