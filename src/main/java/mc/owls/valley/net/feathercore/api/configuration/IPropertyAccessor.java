package mc.owls.valley.net.feathercore.api.configuration;

import java.util.List;
import java.util.Set;

public interface IPropertyAccessor {
    public String getString(final String path);

    public boolean getBoolean(final String path);

    public boolean getBoolean(final String path, final boolean defaultValue);

    public int getInt(final String path);

    public double getDouble(final String path);

    public IConfigSection getConfigurationSection(final String path);

    public List<String> getStringList(final String path);

    public Set<String> getKeys(final boolean recurse);
}
