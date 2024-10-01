/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file YamlUtils.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class
 */

package mc.owls.valley.net.feathercore.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public class YamlUtils {
    public static FileConfiguration loadYaml(final JavaPlugin plugin, final String fileName)
            throws FeatherSetupException {
        FileConfiguration fileConfiguration = null;

        try (final InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                final InputStreamReader reader = new InputStreamReader(inputStream);
                fileConfiguration = YamlConfiguration.loadConfiguration(reader);
            } else {
                throw new FeatherSetupException(fileName
                        + " not found in plugin resources. That's weird O.o, please contact the developer");
            }
        } catch (IOException e) {
            throw new FeatherSetupException(
                    "Error on parsing resource -> cause: " + StringUtils.exceptionToStr(e));
        }

        if (fileConfiguration == null) {
            throw new FeatherSetupException("Failed to read yaml resource: " + fileName);
        }

        return fileConfiguration;
    }
}
