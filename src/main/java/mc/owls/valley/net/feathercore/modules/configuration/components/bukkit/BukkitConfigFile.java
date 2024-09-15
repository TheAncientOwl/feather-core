package mc.owls.valley.net.feathercore.modules.configuration.components.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;

public class BukkitConfigFile implements IConfigFile {
    private final JavaPlugin plugin;
    private final String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public BukkitConfigFile(final JavaPlugin plugin, final String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        this.configFile = new File(plugin.getDataFolder(), fileName);

        saveDefaultConfig();
        loadConfig();
    }

    @Override
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }

    @Override
    public void saveConfig() throws IOException {
        this.fileConfiguration.save(this.configFile);
    }

    @Override
    public void loadConfig() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public void reloadConfig() {
        loadConfig();
    }

    @Override
    public String getString(final String path) {
        return this.fileConfiguration.getString(path);
    }

    @Override
    public IConfigSection getConfigurationSection(final String path) {
        return new BukkitConfigSection(this.fileConfiguration.getConfigurationSection(path));
    }

    @Override
    public boolean getBoolean(final String path) {
        return this.fileConfiguration.getBoolean(path);
    }

    @Override
    public int getInt(final String path) {
        return this.fileConfiguration.getInt(path);
    }

    @Override
    public double getDouble(final String path) {
        return this.fileConfiguration.getDouble(path);
    }

    @Override
    public List<String> getStringList(final String path) {
        return this.fileConfiguration.getStringList(path);
    }

    @Override
    public boolean getBoolean(final String path, final boolean defaultValue) {
        return this.fileConfiguration.getBoolean(path, defaultValue);
    }

    @Override
    public Set<String> getKeys(final boolean recurse) {
        return this.fileConfiguration.getKeys(recurse);
    }

}
