package mc.owls.valley.net.feathercore.core.configuration.bukkit;

import java.util.List;

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

}
