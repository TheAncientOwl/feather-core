/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherEconomyTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit FeatherEconomy#0.2
 * @description Unit tests for FeatherEconomy
 */

package dev.defaultybuf.feathercore.modules.economy.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.defaultybuf.feathercore.api.configuration.IConfigFile;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
class FeatherEconomyTest {
    @Mock IPlayersData mockPlayersData;
    @Mock IConfigFile mockConfig;
    @Mock OfflinePlayer mockPlayer;
    @Mock PlayerModel mockPlayerModel;

    FeatherEconomy featherEconomy;

    @BeforeEach
    public void setUp() {
        featherEconomy = new FeatherEconomy(mockPlayersData, mockConfig);
    }

    @Test
    public void testIsEnabled() {
        assertTrue(featherEconomy.isEnabled());
    }

    @Test
    public void testGetName() {
        assertEquals("FeatherEconomy", featherEconomy.getName());
    }

    @Test
    public void testCurrencyNamePlural() {
        when(mockConfig.getString("currency.name.plural")).thenReturn("Dollars");
        assertEquals("Dollars", featherEconomy.currencyNamePlural());
    }

    @Test
    public void testCurrencyNameSingular() {
        when(mockConfig.getString("currency.name.singular")).thenReturn("Dollar");
        assertEquals("Dollar", featherEconomy.currencyNameSingular());
    }

    @Test
    public void testFormat() {
        when(mockConfig.getString("currency-format")).thenReturn("#,###.00");
        when(mockConfig.getString("currency.name.plural")).thenReturn("Dollars");
        when(mockConfig.getString("currency.name.singular")).thenReturn("Dollar");
        assertEquals("1,000.00Dollars", featherEconomy.format(1000));
        assertEquals("1.00Dollar", featherEconomy.format(1));
    }

    @Test
    public void testDepositPlayer_StringWorldName() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.depositPlayer("player1", "world", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(150.0, response.balance);
    }

    @Test
    public void testDepositPlayer_OfflinePlayerWorldName() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.depositPlayer(mockPlayer, "world", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(150.0, response.balance);
    }

    @Test
    public void testGetBalance_PlayerModel() {
        mockPlayerModel.balance = 100.0;
        assertEquals(100.0, featherEconomy.getBalance(mockPlayerModel));
    }

    @Test
    public void testGetBalance_NullPlayerModel() {
        PlayerModel nullPlayerModel = null;
        assertEquals(-1, featherEconomy.getBalance(nullPlayerModel));
    }

    @Test
    public void testGetBalance_String() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertEquals(100.0, featherEconomy.getBalance("player1"));
    }

    @Test
    public void testGetBalance_OfflinePlayer() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertEquals(100.0, featherEconomy.getBalance(mockPlayer));
    }

    @Test
    public void testHas_StringDouble() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertTrue(featherEconomy.has("player1", 50.0));
    }

    @Test
    public void testHas_OfflinePlayerDouble() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertTrue(featherEconomy.has(mockPlayer, 50.0));
    }

    @Test
    public void testHas_NullPlayerModel() {
        PlayerModel nullPlayerModel = null;
        assertFalse(featherEconomy.has(nullPlayerModel, 50.0));
    }

    @Test
    public void testHas_StringWorldNameDouble() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertTrue(featherEconomy.has("player1", "world", 50.0));
    }

    @Test
    public void testHas_PlayerModel_Null() {
        PlayerModel nullPlayerModel = null;
        assertFalse(featherEconomy.has(nullPlayerModel, 50.0));
    }

    @Test
    public void testHas_PlayerModel_BalanceGreaterThanAmount() {
        mockPlayerModel.balance = 100.0;
        assertTrue(featherEconomy.has(mockPlayerModel, 50.0));
    }

    @Test
    public void testHas_PlayerModel_BalanceEqualToAmount() {
        mockPlayerModel.balance = 50.0;
        assertTrue(featherEconomy.has(mockPlayerModel, 50.0));
    }

    @Test
    public void testHas_PlayerModel_BalanceLessThanAmount() {
        mockPlayerModel.balance = 30.0;
        assertFalse(featherEconomy.has(mockPlayerModel, 50.0));
    }

    @Test
    public void testHas_OfflinePlayerWorldNameDouble() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertTrue(featherEconomy.has(mockPlayer, "world", 50.0));
    }

    @Test
    public void testWithdrawPlayer_StringWorldName() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.withdrawPlayer("player1", "world", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(50.0, response.balance);
    }

    @Test
    public void testWithdrawPlayer_OfflinePlayerWorldName() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.withdrawPlayer(mockPlayer, "world", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(50.0, response.balance);
    }

    @Test
    public void testWithdrawPlayer_String() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.withdrawPlayer("player1", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(50.0, response.balance);
    }

    @Test
    public void testWithdrawPlayer_OfflinePlayer() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.withdrawPlayer(mockPlayer, 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(50.0, response.balance);
    }

    @Test
    public void testDepositPlayer_String() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.depositPlayer("player1", 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(150.0, response.balance);
    }

    @Test
    public void testDepositPlayer_OfflinePlayer() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;

        EconomyResponse response = featherEconomy.depositPlayer(mockPlayer, 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(150.0, response.balance);
    }

    @Test
    public void testDepositPlayer_NullPlayerModel() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(null);

        EconomyResponse response = featherEconomy.depositPlayer(mockPlayer, 50.0);
        assertEquals(ResponseType.FAILURE, response.type);
        assertEquals(0, response.balance);
    }

    @Test
    public void testCreateBank_StringString() {
        EconomyResponse response = featherEconomy.createBank("bank1", "player1");
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testCreateBank_StringOfflinePlayer() {
        EconomyResponse response = featherEconomy.createBank("bank1", mockPlayer);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testDeleteBank() {
        EconomyResponse response = featherEconomy.deleteBank("bank1");
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testBankBalance() {
        EconomyResponse response = featherEconomy.bankBalance("bank1");
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testBankHas() {
        EconomyResponse response = featherEconomy.bankHas("bank1", 100.0);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testBankWithdraw() {
        EconomyResponse response = featherEconomy.bankWithdraw("bank1", 100.0);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testBankDeposit() {
        EconomyResponse response = featherEconomy.bankDeposit("bank1", 100.0);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testIsBankOwner_StringString() {
        EconomyResponse response = featherEconomy.isBankOwner("bank1", "player1");
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testIsBankOwner_StringOfflinePlayer() {
        EconomyResponse response = featherEconomy.isBankOwner("bank1", mockPlayer);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testIsBankMember_StringString() {
        EconomyResponse response = featherEconomy.isBankMember("bank1", "player1");
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testIsBankMember_StringOfflinePlayer() {
        EconomyResponse response = featherEconomy.isBankMember("bank1", mockPlayer);
        assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
    }

    @Test
    public void testGetBanks() {
        List<String> banks = featherEconomy.getBanks();
        assertNotNull(banks);
    }

    @Test
    public void testCreatePlayerAccount_OfflinePlayer() {
        assertFalse(featherEconomy.createPlayerAccount(mockPlayer));
    }

    @Test
    public void testCreatePlayerAccount_String() {
        assertFalse(featherEconomy.createPlayerAccount("player1"));
    }

    @Test
    public void testCreatePlayerAccount_OfflinePlayerWorldName() {
        assertFalse(featherEconomy.createPlayerAccount(mockPlayer, "world"));
    }

    @Test
    public void testCreatePlayerAccount_StringWorldName() {
        assertFalse(featherEconomy.createPlayerAccount("player1", "world"));
    }

    @Test
    public void testFractionalDigits() {
        when(mockConfig.getInt("fractional-digits")).thenReturn(2);
        assertEquals(2, featherEconomy.fractionalDigits());
    }

    @Test
    public void testGetBalance_StringWorld() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertEquals(100.0, featherEconomy.getBalance("player1", "world"));
    }

    @Test
    public void testGetBalance_OfflinePlayerWorld() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        mockPlayerModel.balance = 100.0;
        assertEquals(100.0, featherEconomy.getBalance(mockPlayer, "world"));
    }

    @Test
    public void testWithdrawPlayer_PlayerModel_Success() {
        mockPlayerModel.balance = 100.0;
        EconomyResponse response = featherEconomy.withdrawPlayer(mockPlayerModel, 50.0);
        assertEquals(ResponseType.SUCCESS, response.type);
        assertEquals(50.0, response.balance);
    }

    @Test
    public void testWithdrawPlayer_PlayerModel_Failure() {
        EconomyResponse response = featherEconomy.withdrawPlayer((PlayerModel) null, 50.0);
        assertEquals(ResponseType.FAILURE, response.type);
        assertEquals(0, response.balance);
    }

    @Test
    public void testHasAccount_String() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount("player1"));
    }

    @Test
    public void testHasAccount_OfflinePlayer() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount(mockPlayer));
    }

    @Test
    public void testHasAccount_StringWorld() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount("player1", "world"));
    }

    @Test
    public void testHasAccount_OfflinePlayerWorld() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount(mockPlayer, "world"));
    }

    @Test
    public void testHasBankSupport() {
        assertFalse(featherEconomy.hasBankSupport());
    }

    @Test
    public void testCreatePlayerAccount_PlayerExists() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.createPlayerAccount("player1"));
    }

    @Test
    public void testCreatePlayerAccount_PlayerDoesNotExist() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(null);
        assertFalse(featherEconomy.createPlayerAccount("player1"));
    }

    @Test
    public void testCreatePlayerAccount_OfflinePlayerExists() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.createPlayerAccount(mockPlayer));
    }

    @Test
    public void testCreatePlayerAccount_OfflinePlayerDoesNotExist() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(null);
        assertFalse(featherEconomy.createPlayerAccount(mockPlayer));
    }

    @Test
    public void testHasAccount_PlayerExists() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount("player1"));
    }

    @Test
    public void testHasAccount_PlayerDoesNotExist() {
        when(mockPlayersData.getPlayerModel("player1")).thenReturn(null);
        assertFalse(featherEconomy.hasAccount("player1"));
    }

    @Test
    public void testHasAccount_OfflinePlayerExists() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(mockPlayerModel);
        assertTrue(featherEconomy.hasAccount(mockPlayer));
    }

    @Test
    public void testHasAccount_OfflinePlayerDoesNotExist() {
        when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(null);
        assertFalse(featherEconomy.hasAccount(mockPlayer));
    }
}
