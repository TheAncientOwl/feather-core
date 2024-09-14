package mc.owls.valley.net.feathercore.api.module;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface IConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();

    public IConfigFile getMessagesConfigFile();

    public IConfigFile getPvPConfigFile();
}
