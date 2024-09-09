package mc.owls.valley.net.feathercore.api.core;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface IConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();

    public IConfigFile getMessagesConfigFile();
}
