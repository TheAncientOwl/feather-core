package mc.owls.valley.net.feathercore.api.configuration;

import java.io.IOException;

public interface IConfigFile extends IPropertyAccessor {
    public void saveDefaultConfig();

    public void saveConfig() throws IOException;

    public void loadConfig();

    public void reloadConfig();
}
