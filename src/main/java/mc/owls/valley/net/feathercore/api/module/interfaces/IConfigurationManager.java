package mc.owls.valley.net.feathercore.api.module.interfaces;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface IConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();

    public IConfigFile getMessagesConfigFile();

    public IConfigFile getPvPConfigFile();
}
