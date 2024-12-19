/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageChangeEventTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit LanguageChangeEvent#0.2
 * @description Unit tests for LanguageChangeEvent
 */

package mc.owls.valley.net.feathercore.modules.language.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

class LanguageChangeEventTest {
    Player mockPlayer = null;
    IConfigFile mockTranslation = null;
    LanguageChangeEvent event = null;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockTranslation = mock(IConfigFile.class);
        event = new LanguageChangeEvent(mockPlayer, "en", mockTranslation);
    }

    @Test
    void testConstructor_ShouldInitializeFieldsCorrectly() {
        assertEquals(mockPlayer, event.getPlayer());
        assertEquals("en", event.getLanguage());
        assertEquals(mockTranslation, event.getTranslation());
        assertFalse(event.isCancelled(), "Event should not be cancelled by default");
    }

    @Test
    void testSetCancelled_ShouldChangeCancelledState() {
        assertFalse(event.isCancelled(), "Event should not be cancelled by default");

        event.setCancelled(true);
        assertTrue(event.isCancelled(), "Event should be marked as cancelled");

        event.setCancelled(false);
        assertFalse(event.isCancelled(), "Event should be marked as not cancelled");
    }

    @Test
    void testGetHandlers_ShouldReturnHandlerList() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers, "HandlerList should not be null");
        assertSame(handlers, LanguageChangeEvent.getHandlerList(),
                "getHandlers and getHandlerList should return the same instance");
    }
}
