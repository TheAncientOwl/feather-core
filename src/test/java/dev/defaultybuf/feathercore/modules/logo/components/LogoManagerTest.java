/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LogoManagerTest.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @test_unit LogoManager#0.5
 * @description Unit tests for LogoManager
 */

package dev.defaultybuf.feathercore.modules.logo.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feather.toolkit.testing.mockers.FeatherModuleTest;

class LogoManagerTest extends FeatherModuleTest<LogoManager> {
    static final String PLUGIN_YML_CONTENT = "version: 'test-plugin-version'";

    @Mock ConsoleCommandSender mockConsoleCommandSender;

    public static class DummyConfig {
    }

    @Override
    protected Class<LogoManager> getModuleClass() {
        return LogoManager.class;
    }

    @Override
    protected String getModuleName() {
        return "$LogoManager";
    }

    @Test
    void basics() {
        when(mockServer.getConsoleSender()).thenReturn(mockConsoleCommandSender);
        when(mockServer.getVersion()).thenReturn("test-server-version");
        var pluginStream = new ByteArrayInputStream(PLUGIN_YML_CONTENT.getBytes());
        when(mockJavaPlugin.getResource("plugin.yml")).thenReturn(pluginStream);

        assertDoesNotThrow(() -> {
            moduleInstance.onModuleEnable();
        });

        verify(mockConsoleCommandSender, atLeast(2)).sendMessage(anyString());

        clearInvocations(mockConsoleCommandSender);

        assertDoesNotThrow(() -> {
            moduleInstance.onModuleDisable();
        });

        verify(mockConsoleCommandSender, atLeast(2)).sendMessage(anyString());
    }

}
