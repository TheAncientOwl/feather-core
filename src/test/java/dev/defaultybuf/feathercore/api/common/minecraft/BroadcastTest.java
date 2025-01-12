/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BroadcastTest.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @test_unit Broadcast#0.4
 * @description Unit tests for Broadcast
 */

package dev.defaultybuf.feathercore.api.common.minecraft;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.configuration.IPropertyAccessor;

@ExtendWith(MockitoExtension.class)
class BroadcastTest {
    @Mock IPropertyAccessor mockPropertyAccessor = null;
    @Mock Player mockPlayer1 = null;
    @Mock Player mockPlayer2 = null;

    MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockPlayer1, mockPlayer2));
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.close();
    }

    @Test
    void testBroadcastSingleMessage() {
        // Prepare test data
        var message = "Test message";

        // Call the method under test
        Broadcast.broadcast(message);

        // Verify that the sendMessage method is called for both players
        verify(mockPlayer1).sendMessage(message);
        verify(mockPlayer2).sendMessage(message);
    }

    @Test
    void testBroadcastMultipleMessages() {
        // Prepare test data
        var messages = new String[] {"Message 1", "Message 2"};

        // Mock StringUtils.translateColors method
        try (var mocked = mockStatic(StringUtils.class)) {
            mocked.when(() -> StringUtils.translateColors(anyString()))
                    .thenReturn("translated message");

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
        var placeholders = List.of(Pair.of("{name}", (Object) "World"));

        // Mock StringUtils.replacePlaceholders method
        try (var mocked = mockStatic(StringUtils.class)) {
            mocked.when(() -> StringUtils.translateColors(anyString())).thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
            mocked.when(() -> StringUtils.replacePlaceholders(anyString(), anyList()))
                    .thenReturn("Hello World!");

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
        var mockPropertyAccessor = mock(IPropertyAccessor.class);

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
        var mockPropertyAccessor = mock(IPropertyAccessor.class);

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
