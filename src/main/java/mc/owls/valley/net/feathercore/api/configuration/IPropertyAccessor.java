package mc.owls.valley.net.feathercore.api.configuration;

import java.util.List;

public interface IPropertyAccessor {
    public String getString(final String path);

    public boolean getBoolean(final String path);

    public int getInt(final String path);

    public double getDouble(final String path);

    public IConfigSection getConfigurationSection(final String path);

    public List<String> getStringList(final String path);
}
