/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BroadcastTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit Broadcast#0.4
 * @description Unit tests for Broadcast
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.MockedStatic;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;

class BroadcastTest {

    private IPropertyAccessor mockPropertyAccessor = null;
    private Player mockPlayer1 = null;
    private Player mockPlayer2 = null;
    private MockedStatic<Bukkit> mockedBukkit = null;

    @BeforeEach
    void setUp() {
        // Mock Bukkit's online players
        mockPlayer1 = mock(Player.class);
        mockPlayer2 = mock(Player.class);

        // Mock the property accessor
        mockPropertyAccessor = mock(IPropertyAccessor.class);

        mockedBukkit = mockStatic(Bukkit.class);

        // Mock the Bukkit server's online players
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockPlayer1, mockPlayer2));
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.close();
    }

    @Test
    void testBroadcastSingleMessage() {
        // Prepare test data
        String message = "Test message";

        // Call the method under test
        Broadcast.broadcast(message);

        // Verify that the sendMessage method is called for both players
        verify(mockPlayer1).sendMessage(message);
        verify(mockPlayer2).sendMessage(message);
    }

    @Test
    void testBroadcastMultipleMessages() {
        // Prepare test data
        String[] messages = { "Message 1", "Message 2" };

        // Mock StringUtils.translateColors method
        try (var mocked = mockStatic(StringUtils.class)) {
            mocked.when(() -> StringUtils.translateColors(anyString())).thenReturn("translated message");

            // Call the method under test
            Broadcast.broadcast(messages);

            // Verify that the sendMessage method is called for both players with the
            // translated message
            verify(mockPlayer1).sendMessage("translated message");
            verify(mockPlayer2).sendMessage("translated message");
        }
    }

    @Test
    void testBroadcastFromPropertyAccessorSingleKey() {
        // Mock property accessor behavior
        when(mockPropertyAccessor.getString("key")).thenReturn("Property message");

        // Call the method under test
        Broadcast.broadcast(mockPropertyAccessor, "key");

        // Verify that the sendMessage method is called for both players with the
        // translated message
        verify(mockPlayer1).sendMessage("Property message");
        verify(mockPlayer2).sendMessage("Property message");
    }

    @Test
    void testBroadcastFromPropertyAccessorMultipleKeys() {
        // Mock property accessor behavior
        when(mockPropertyAccessor.getString("key1")).thenReturn("Message 1");
        when(mockPropertyAccessor.getString("key2")).thenReturn("Message 2");

        // Call the method under test
        Broadcast.broadcast(mockPropertyAccessor, "key1", "key2");

        // Verify that the sendMessage method is called for both players with the
        // concatenated message
        verify(mockPlayer1).sendMessage("Message 1\nMessage 2");
        verify(mockPlayer2).sendMessage("Message 1\nMessage 2");
    }

    @Test
    void testBroadcastWithPlaceholders() {
        // Mock property accessor behavior
        when(mockPropertyAccessor.getString("key")).thenReturn("Hello {name}!");

        // Create a list of placeholders
        List<Pair<String, Object>> placeholders = List.of(Pair.of("{name}", "World"));

        // Mock StringUtils.replacePlaceholders method
        try (var mocked = mockStatic(StringUtils.class)) {
            mocked.when(() -> StringUtils.translateColors(anyString())).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            mocked.when(() -> StringUtils.replacePlaceholders(anyString(), anyList())).thenReturn("Hello World!");

            // Call the method under test
            Broadcast.broadcast(mockPropertyAccessor, "key", placeholders);

            // Verify that the sendMessage method is called for both players with the final
            // message
            verify(mockPlayer1).sendMessage("Hello World!");
            verify(mockPlayer2).sendMessage("Hello World!");
        }
    }

    @Test
    void testBroadcastWithNonEmptyStringBuilder() {
        // Mock dependencies
        IPropertyAccessor mockPropertyAccessor = mock(IPropertyAccessor.class);

        // Mock the propertyAccessor to return some strings
        when(mockPropertyAccessor.getString("key1")).thenReturn("Hello");
        when(mockPropertyAccessor.getString("key2")).thenReturn("World");

        // Call the method under test
        Broadcast.broadcast(mockPropertyAccessor, "key1", "key2");

        // Verify that the message is sent
        verify(mockPlayer1).sendMessage("Hello\nWorld");
        verify(mockPlayer2).sendMessage("Hello\nWorld");
    }

    @Test
    void testBroadcastWithEmptyStringBuilder() {
        // Mock dependencies
        IPropertyAccessor mockPropertyAccessor = mock(IPropertyAccessor.class);

        // Call the method under test with no keys
        Broadcast.broadcast(mockPropertyAccessor);

        // Verify that nothing is sent if there are no keys to append
        verify(mockPlayer1).sendMessage("");
        verify(mockPlayer2).sendMessage("");
    }

    @Test
    void dummyConstructor() {
        @SuppressWarnings("unused")
        var Broadcast = new Broadcast(); // Broadcast should contain only static methods
    }
}
