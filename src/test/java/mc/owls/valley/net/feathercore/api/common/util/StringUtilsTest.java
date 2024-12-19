/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file StringUtilsTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit StringUtils#0.3
 * @description Unit tests for StringUtils
 */

package mc.owls.valley.net.feathercore.api.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import net.md_5.bungee.api.ChatColor;

class StringUtilsTest {

    @BeforeEach
    void setUp() {
        // Set up necessary conditions for each test if needed
    }

    @Test
    void testExceptionToStr() {
        Exception exception = new Exception("Test exception");
        String result = StringUtils.exceptionToStr(exception);
        assertNotNull(result);
        assertTrue(result.contains("Test exception"));
    }

    @Test
    void testReplacePlaceholdersSingleReplacement() {
        String message = "Hello, {name}!";
        Pair<String, Object> replacement = Pair.of("{name}", "John");
        String result = StringUtils.replacePlaceholders(message, replacement);
        assertEquals("Hello, John!", result);
    }

    @Test
    void testReplacePlaceholdersMultipleReplacements() {
        String message = "Hello, {name}! Your balance is {balance}.";
        List<Pair<String, Object>> replacements = new ArrayList<>();
        replacements.add(Pair.of("{name}", "John"));
        replacements.add(Pair.of("{balance}", 100));

        String result = StringUtils.replacePlaceholders(message, replacements);
        assertEquals("Hello, John! Your balance is 100.", result);
    }

    @Test
    void testGetOnlinePlayers() {
        // Mock the Bukkit API
        Player mockPlayer1 = mock(Player.class);
        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer1.getName()).thenReturn("Player1");
        when(mockPlayer2.getName()).thenReturn("Player2");

        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            // Return the mock players when Bukkit.getOnlinePlayers() is called
            mockedBukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(mockPlayer1, mockPlayer2));

            // Test the method
            List<String> players = StringUtils.getOnlinePlayers();
            assertNotNull(players);
            assertEquals(2, players.size());
            assertTrue(players.contains("Player1"));
            assertTrue(players.contains("Player2"));
        }
    }

    @Test
    void testGetWorlds() {
        // Mock the Bukkit API
        World mockWorld1 = mock(World.class);
        World mockWorld2 = mock(World.class);
        when(mockWorld1.getName()).thenReturn("World1");
        when(mockWorld2.getName()).thenReturn("World2");

        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            // Return the mock worlds when Bukkit.getWorlds() is called
            mockedBukkit.when(Bukkit::getWorlds).thenReturn(List.of(mockWorld1, mockWorld2));

            List<String> worlds = StringUtils.getWorlds();
            assertNotNull(worlds);
            assertEquals(2, worlds.size());
            assertTrue(worlds.contains("World1"));
            assertTrue(worlds.contains("World2"));
        }
    }

    @Test
    void testFilterStartingWith() {
        List<String> list = List.of("Apple", "Banana", "Cherry", "Avocado");
        List<String> result = StringUtils.filterStartingWith(list, "A");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Apple"));
        assertTrue(result.contains("Avocado"));
    }

    @Test
    void testTranslateColors() {
        String message = "&aGreen &cRed";
        String result = StringUtils.translateColors(message);
        assertEquals(ChatColor.GREEN + "Green " + ChatColor.RED + "Red", result);
    }

    @Test
    void dummyConstructor() {
        @SuppressWarnings("unused")
        var StringUtils = new StringUtils(); // StringUtils should contain only static methods
    }
}
