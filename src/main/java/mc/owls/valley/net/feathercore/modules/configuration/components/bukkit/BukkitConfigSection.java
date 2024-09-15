package mc.owls.valley.net.feathercore.modules.configuration.components.bukkit;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;

public class BukkitConfigSection implements IConfigSection {
    private final ConfigurationSection configSection;

    public BukkitConfigSection(final ConfigurationSection configSection) {
        this.configSection = configSection;
    }

    @Override
    public String getString(final String path) {
        return this.configSection.getString(path);
    }

    @Override
    public boolean getBoolean(final String path) {
        return this.configSection.getBoolean(path);
    }

    @Override
    public int getInt(final String path) {
        return this.configSection.getInt(path);
    }

    @Override
    public double getDouble(final String path) {
        return this.configSection.getDouble(path);
    }

    @Override
    public IConfigSection getConfigurationSection(final String path) {
        return new BukkitConfigSection(this.configSection.getConfigurationSection(path));
    }

    @Override
    public List<String> getStringList(final String path) {
        return this.configSection.getStringList(path);
    }

    @Override
    public boolean getBoolean(final String path, final boolean defaultValue) {
        return this.configSection.getBoolean(path, defaultValue);
    }

    @Override
    public Set<String> getKeys(final boolean recurse) {
        return this.configSection.getKeys(recurse);
    }

}
