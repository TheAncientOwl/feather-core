package mc.owls.valley.net.feathercore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;

public class YamlUtils {
    public static FileConfiguration loadYaml(@NotNull final FeatherCore plugin, @NotNull final String fileName)
            throws ModuleSetupException {
        FileConfiguration fileConfiguration = null;

        try (final InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                final InputStreamReader reader = new InputStreamReader(inputStream);
                fileConfiguration = YamlConfiguration.loadConfiguration(reader);
            } else {
                throw new ModuleSetupException(fileName
                        + " not found in plugin resources. That's weird O.o, please contact the developer");
            }
        } catch (IOException e) {
            throw new ModuleSetupException(
                    "Error on parsing resource -> cause: " + StringUtils.exceptionToStr(e));
        }

        if (fileConfiguration == null) {
            throw new ModuleSetupException("Failed to read yaml resource: " + fileName);
        }

        return fileConfiguration;
    }
}
