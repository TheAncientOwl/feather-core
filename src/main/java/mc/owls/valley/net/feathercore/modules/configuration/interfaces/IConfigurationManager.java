package mc.owls.valley.net.feathercore.modules.configuration.interfaces;

public interface IConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();

    public IConfigFile getPvPConfigFile();

    public IConfigFile getTranslationsConfigFile();

    public IConfigFile getLootChestsConfigFile();
}
