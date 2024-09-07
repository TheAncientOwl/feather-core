package mc.owls.valley.net.feathercore.api.configuration;

public interface IConfigFile extends IPropertyAccessor {
    public void saveDefaultConfig();

    public void saveConfig();

    public void loadConfig();

    public void reloadConfig();
}
