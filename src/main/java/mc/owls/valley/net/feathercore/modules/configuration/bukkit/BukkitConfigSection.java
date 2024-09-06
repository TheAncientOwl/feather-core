package mc.owls.valley.net.feathercore.modules.configuration.bukkit;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigSection;

public class BukkitConfigSection implements IConfigSection {
    private final ConfigurationSection configSection;

    public BukkitConfigSection(final ConfigurationSection configSection) {
        this.configSection = configSection;
    }

    @Override
    public String getString(@NotNull String path) {
        return this.configSection.getString(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return this.configSection.getBoolean(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return this.configSection.getInt(path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return this.configSection.getDouble(path);
    }

    @Override
    public IConfigSection getConfigurationSection(@NotNull String path) {
        return new BukkitConfigSection(this.configSection.getConfigurationSection(path));
    }

}
