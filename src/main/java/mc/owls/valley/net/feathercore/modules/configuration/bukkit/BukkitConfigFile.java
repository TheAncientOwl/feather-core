package mc.owls.valley.net.feathercore.modules.configuration.bukkit;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigSection;
import mc.owls.valley.net.feathercore.utils.StringUtils;

public class BukkitConfigFile implements IConfigFile {
    private final FeatherCore plugin;
    private final String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public BukkitConfigFile(final FeatherCore plugin, final String fileName) {
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
    public void saveConfig() {
        try {
            this.fileConfiguration.save(this.configFile);
        } catch (final IOException e) {
            this.plugin.getFeatherLogger()
                    .error("Could not save config to " + this.fileName + "\nReason: " + StringUtils.exceptionToStr(e));
        }
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
    public String getString(@NotNull final String path) {
        return this.fileConfiguration.getString(path);
    }

    @Override
    public IConfigSection getConfigurationSection(@NotNull String path) {
        return new BukkitConfigSection(this.fileConfiguration.getConfigurationSection(path));
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return this.fileConfiguration.getBoolean(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return this.fileConfiguration.getInt(path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return this.fileConfiguration.getDouble(path);
    }

}
