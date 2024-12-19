/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file YamlUtils.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit YamlUtils#0.1
 * @description Unit tests for YamlUtils
 */

package mc.owls.valley.net.feathercore.api.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

class YamlUtilsTest {

    @Test
    void testLoadYaml() throws Exception {
        // Mock the plugin and set up resource handling
        JavaPlugin mockPlugin = mock(JavaPlugin.class);
        String fileName = "valid.yml";

        String yamlContent = "value: 25\nother-value: 30";
        InputStream mockInputStream = new ByteArrayInputStream(yamlContent.getBytes());

        // Mock the behavior of getResource to return your InputStream
        when(mockPlugin.getResource(fileName)).thenReturn(mockInputStream);

        // Call the method under test
        FileConfiguration fileConfiguration = YamlUtils.loadYaml(mockPlugin, fileName);

        // Assertions to check if the configuration is correctly loaded
        assertNotNull(fileConfiguration);
        assertEquals(25, fileConfiguration.getInt("value"));
        assertEquals(30, fileConfiguration.getInt("other-value"));
    }

    @Test
    void testLoadYaml_FileNotFound() {
        // Mock JavaPlugin
        JavaPlugin mockPlugin = mock(JavaPlugin.class);

        when(mockPlugin.getResource("missing.yml")).thenReturn(null);

        // Call method under test and assert exception
        FeatherSetupException exception = assertThrows(FeatherSetupException.class, () -> {
            YamlUtils.loadYaml(mockPlugin, "missing.yml");
        });

        assertTrue(exception.getMessage().contains("missing.yml not found in plugin resources"),
                "Exception message should indicate missing file");
    }

    @Test
    void testLoadYaml_IOException() {
        // Mock JavaPlugin and input stream that throws IOException
        JavaPlugin mockPlugin = mock(JavaPlugin.class);
        InputStream mockInputStream = mock(InputStream.class);

        when(mockPlugin.getResource("invalid.yml")).thenReturn(mockInputStream);
        try {
            doThrow(new IOException("Stream read error")).when(mockInputStream).close();
        } catch (IOException e) {
            fail("Mock setup failed");
        }

        // Call method under test and assert exception
        FeatherSetupException exception = assertThrows(FeatherSetupException.class, () -> {
            YamlUtils.loadYaml(mockPlugin, "invalid.yml");
        });

        assertTrue(exception.getMessage().contains("Error on parsing resource"),
                "Exception message should indicate parsing error");
    }

    @Test
    void testLoadYaml_NullFileConfiguration() {
        // Mock JavaPlugin and return valid input stream, but simulate null
        // FileConfiguration
        JavaPlugin mockPlugin = mock(JavaPlugin.class);
        InputStream mockInputStream = new ByteArrayInputStream("".getBytes());

        when(mockPlugin.getResource("empty.yml")).thenReturn(mockInputStream);

        // Spy on YamlConfiguration to simulate a null return
        mockStatic(YamlConfiguration.class);
        when(YamlConfiguration.loadConfiguration(any(InputStreamReader.class))).thenReturn(null);

        // Call method under test and assert exception
        FeatherSetupException exception = assertThrows(FeatherSetupException.class, () -> {
            YamlUtils.loadYaml(mockPlugin, "empty.yml");
        });

        assertTrue(exception.getMessage().contains("Failed to read yaml resource"),
                "Exception message should indicate null FileConfiguration");
    }

    @Test
    void dummyConstructor() {
        @SuppressWarnings("unused")
        var YamlUtils = new YamlUtils(); // YamlUtils should contain only static methods
    }
}
