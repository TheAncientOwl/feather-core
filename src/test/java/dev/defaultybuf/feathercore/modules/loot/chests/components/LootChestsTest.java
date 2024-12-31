/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestsTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit LootChests#0.8
 * @description Unit tests for LootChests
 */

package dev.defaultybuf.feathercore.modules.loot.chests.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.api.configuration.IConfigSection;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.ModuleTestMocker;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.interfaces.IMongoDB;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;

class LootChestsTest extends ModuleTestMocker<LootChests> {
    @Mock Player mockPlayer;
    @Mock Inventory mockInventory;
    @Mock LootChestsDAO mockLootChestsDAO;

    @MockedModule(of = Module.MongoDB) IMongoDB mockMongoDB;
    @MockedModule(of = Module.PlayersData) IPlayersData mockPlayersData;

    @TestField PlayerModel playerModel;
    @TestField LootChestsModel lootChestsModel;

    @Override
    protected String getModuleName() {
        return "LootChests";
    }

    @Override
    protected Class<LootChests> getModuleClass() {
        return LootChests.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lootChestsModel = new LootChestsModel();

        lenient().when(mockMongoDB.getLootChestsDAO()).thenReturn(mockLootChestsDAO);
        lenient().when(mockLootChestsDAO.getChests()).thenReturn(lootChestsModel);
        lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);

    }

    @BeforeEach
    void enableModule() {
        assertDoesNotThrow(() -> {
            moduleInstance.onModuleEnable();
        });
    }

    @AfterEach
    void disableModule() {
        assertDoesNotThrow(() -> {
            moduleInstance.onDisable(mockFeatherLogger);
        });
    }

    @Test
    void testOnModuleEnable() throws FeatherSetupException {
        moduleInstance.onModuleEnable();

        verify(mockMongoDB.getLootChestsDAO(), times(2)).getChests();
    }

    @Test
    void testOnModuleDisable() {
        moduleInstance.onModuleDisable();

        verify(mockMongoDB.getLootChestsDAO()).save(lootChestsModel);
    }

    @Test
    void testSetChest() {
        String location = "world,0,64,0";
        String type = "testType";

        moduleInstance.setChest(location, type);

        assertTrue(lootChestsModel.locationToType.containsKey(location));
        assertEquals(type, lootChestsModel.locationToType.get(location));
        verify(mockMongoDB.getLootChestsDAO()).save(lootChestsModel);
    }

    @Test
    void testUnsetChest() {
        String location = "world,0,64,0";
        lootChestsModel.locationToType.put(location, "testType");

        moduleInstance.unsetChest(location);

        assertFalse(lootChestsModel.locationToType.containsKey(location));
        verify(mockMongoDB.getLootChestsDAO()).save(lootChestsModel);
    }

    @Test
    void testGetChestType() {
        String location = "world,0,64,0";
        String type = "testType";
        lootChestsModel.locationToType.put(location, type);

        String result = moduleInstance.getChestType(location);

        assertEquals(type, result);
    }

    @Test
    void testGetChestType_NotFound() {
        String location = "world,0,64,0";

        String result = moduleInstance.getChestType(location);

        assertNull(result);
    }

    @Test
    void testGetChestLocations() {
        String type = "testType";
        String location1 = "world,0,64,0";
        String location2 = "world,1,64,0";
        String location3 = "world,2,64,0";
        lootChestsModel.locationToType.put(location1, type);
        lootChestsModel.locationToType.put(location2, type);
        lootChestsModel.locationToType.put(location3, "otherTestType");

        List<String> result = moduleInstance.getChestLocations(type);

        assertEquals(2, result.size());
        assertTrue(result.contains(location1));
        assertTrue(result.contains(location2));
    }

    @Test
    void testGetChestLocations_NotFound() {
        String type = "testType";

        List<String> result = moduleInstance.getChestLocations(type);

        assertEquals(List.of(), result);
    }

    @Test
    void testIsChestType() {
        String type = "testType";
        IConfigSection mockSection = mock(IConfigSection.class);
        when(mockModuleConfig.getConfigurationSection("chests")).thenReturn(mockSection);
        when(mockSection.getKeys(false)).thenReturn(Set.of(type, "otherTestType"));

        boolean result = moduleInstance.isChestType(type);

        assertTrue(result);
    }

    @Test
    void testIsChestType_NotFound() {
        String type = "testType";
        IConfigSection mockSection = mock(IConfigSection.class);
        when(mockModuleConfig.getConfigurationSection("chests")).thenReturn(mockSection);
        when(mockSection.getKeys(false)).thenReturn(Set.of("otherTestType"));

        boolean result = moduleInstance.isChestType(type);

        assertFalse(result);
    }

    @Test
    void testDeleteChest() {
        String type = "testType";

        moduleInstance.deleteChest(type);

        verify(mockModuleConfig).remove("chests." + type);
    }

    @Test
    void testGetOpenChestTime() {
        String location = "world,0,64,0";
        Long openTime = System.currentTimeMillis();
        playerModel.chestLocationToOpenTime.put(location, openTime);

        Long result = moduleInstance.getOpenChestTime(mockPlayer, location);

        assertEquals(openTime, result);
    }

    @Test
    void testGetOpenChestTime_NotFound() {
        String location = "world,0,64,0";

        Long result = moduleInstance.getOpenChestTime(mockPlayer, location);

        assertNull(result);
    }

    @Test
    void testOpenChest_Found() {
        String chestType = "testType";
        String location = "world,0,64,0";
        Long now = System.currentTimeMillis();

        when(mockModuleConfig.getInventory("chests." + chestType)).thenReturn(mockInventory);

        moduleInstance.openChest(mockPlayer, chestType, location, now);

        verify(mockPlayer).openInventory(mockInventory);
        assertEquals(now, playerModel.chestLocationToOpenTime.get(location));
        verify(mockPlayersData).markPlayerModelForSave(playerModel);
    }

    @Test
    void testOpenChest_NotFound() {
        String chestType = "testType";
        String location = "world,0,64,0";
        Long now = System.currentTimeMillis();

        when(mockModuleConfig.getInventory("chests." + chestType)).thenReturn(null);

        moduleInstance.openChest(mockPlayer, chestType, location, now);

        verify(mockFeatherLogger).warn(anyString());
        verify(mockPlayersData, never()).markPlayerModelForSave(playerModel);
        assertFalse(playerModel.chestLocationToOpenTime.containsKey(location));
        assertFalse(lootChestsModel.locationToType.containsKey(location));
        verify(mockMongoDB.getLootChestsDAO()).save(lootChestsModel);
    }

    @Test
    void testCreateChest() {
        String type = "testType";
        String displayName = "Test Chest";
        long cooldown = 3600L;
        Inventory inventory = mock(Inventory.class);

        moduleInstance.createChest(type, displayName, cooldown, inventory);

        String configPath = "chests." + type;
        verify(mockModuleConfig).setSeconds(configPath + ".cooldown", cooldown);
        verify(mockModuleConfig).setString(configPath + ".display-name", displayName);
        verify(mockModuleConfig).setInventory(configPath, inventory);

        try {
            verify(mockModuleConfig).saveConfig();
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        verifyNoInteractions(mockFeatherLogger);
    }

    @Test
    void testCreateChest_SaveConfigThrows() throws IOException {
        String type = "testType";
        String displayName = "Test Chest";
        long cooldown = 3600L;
        Inventory inventory = mock(Inventory.class);

        doThrow(new RuntimeException("Test exception")).when(mockModuleConfig).saveConfig();

        moduleInstance.createChest(type, displayName, cooldown, inventory);

        String configPath = "chests." + type;
        verify(mockModuleConfig).setSeconds(configPath + ".cooldown", cooldown);
        verify(mockModuleConfig).setString(configPath + ".display-name", displayName);
        verify(mockModuleConfig).setInventory(configPath, inventory);
        try {
            verify(mockModuleConfig).saveConfig();
        } catch (IOException e) {
        }
        verify(mockFeatherLogger)
                .error(contains("Could not save chest config to file loot-chests.yml"));
    }
}
