/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherEconomy.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Implementation of @see vault AbstractEconomy
 */

package mc.owls.valley.net.feathercore.modules.economy.components;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class FeatherEconomy extends AbstractEconomy {
    private IPlayersData playersDataManager = null;
    private IConfigFile config = null;

    public FeatherEconomy(final IPlayersData playersDataManager, final IConfigFile config) {
        this.playersDataManager = playersDataManager;
        this.config = config;
    }

    // ------------------------------[ Configuration ]------------------------------
    /**
     * Checks if economy method is enabled.
     * 
     * @return Success or Failure
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets name of economy method
     * 
     * @return Name of Economy Method
     */
    @Override
    public String getName() {
        return "FeatherEconomy";
    }

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty
     * string will be returned.
     * 
     * @return name of the currency (plural)
     */
    @Override
    public String currencyNamePlural() {
        return this.config.getString("currency.name.plural");
    }

    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty
     * string will be returned.
     * 
     * @return name of the currency (singular)
     */
    @Override
    public String currencyNameSingular() {
        return this.config.getString("currency.name.singular");
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    @Override
    public String format(final double amount) {
        DecimalFormat decimalFormat = new DecimalFormat(this.config.getString("currency-format"));
        return decimalFormat.format(amount);
    }

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     * 
     * @return number of digits after the decimal point kept
     */
    @Override
    public int fractionalDigits() {
        return this.config.getInt("fractional-digits");
    }

    // ------------------------------[ Deposit ]------------------------------
    /**
     * Deposit an amount to a player model - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerModel to deposit to
     * @param amount      to deposit
     * @return Detailed response of transaction
     */
    private EconomyResponse depositPlayer(final PlayerModel playerModel, final double amount) {
        if (playerModel != null) {
            playerModel.balance += amount;
            this.playersDataManager.markPlayerModelForSave(playerModel.uuid);
            return new EconomyResponse(amount, playerModel.balance, ResponseType.SUCCESS, "success");
        } else {
            return new EconomyResponse(0, 0, ResponseType.FAILURE,
                    "Could not deposit the amount; Player data does not exist");
        }
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(final String playerName, final double amount) {
        return depositPlayer(this.playersDataManager.getPlayerModel(playerName), amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to deposit to
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer player, final double amount) {
        return depositPlayer(this.playersDataManager.getPlayerModel(player), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
        return depositPlayer(this.playersDataManager.getPlayerModel(playerName), amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the
     * global balance will be returned.
     * 
     * @param player    to deposit to
     * @param worldName name of the world
     * @param amount    Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer player, final String worldName, final double amount) {
        return depositPlayer(this.playersDataManager.getPlayerModel(player), amount);
    }

    // ------------------------------[ Balance ]------------------------------
    /**
     * Gets balance of a playerModel
     * 
     * @param playerModel of the playerModel
     * @return Amount currently held in playerModel's account
     */
    public double getBalance(final PlayerModel playerModel) {
        return playerModel != null ? playerModel.balance : -1;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)}
     *             instead.
     */
    @Override
    @Deprecated
    public double getBalance(final String playerName) {
        return getBalance(this.playersDataManager.getPlayerModel(playerName));
    }

    /**
     * Gets balance of a player
     * 
     * @param player of the player
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(final OfflinePlayer player) {
        return getBalance(this.playersDataManager.getPlayerModel(player));
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)}
     *             instead.
     */
    @Override
    @Deprecated
    public double getBalance(final String playerName, final String world) {
        return getBalance(playerName);
    }

    /**
     * Gets balance of a player on the specified world.
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the
     * global balance will be returned.
     * 
     * @param player to check
     * @param world  name of the world
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(final OfflinePlayer player, final String world) {
        return getBalance(this.playersDataManager.getPlayerModel(player));
    }

    // ------------------------------[ BalanceCheck ]------------------------------
    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    public boolean has(final PlayerModel playerModel, final double amount) {
        return playerModel != null ? playerModel.balance >= amount : false;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean has(final String playerName, final double amount) {
        return has(this.playersDataManager.getPlayerModel(playerName), amount);
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(final OfflinePlayer player, final double amount) {
        return has(this.playersDataManager.getPlayerModel(player), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use @{link
     *             {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public boolean has(final String playerName, final String worldName, final double amount) {
        return has(playerName, amount);
    }

    /**
     * Checks if the player account has the amount in a given world - DO NOT USE
     * NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the
     * global balance will be returned.
     * 
     * @param player    to check
     * @param worldName to check with
     * @param amount    to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(final OfflinePlayer player, final String worldName, final double amount) {
        return has(player, amount);
    }

    // ------------------------------[ Withddraw ]------------------------------
    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param playerModel to withdraw from
     * @param amount      to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(final PlayerModel playerModel, final double amount) {
        if (playerModel != null) {
            playerModel.balance -= amount;
            this.playersDataManager.markPlayerModelForSave(playerModel.uuid);
            return new EconomyResponse(amount, playerModel.balance, ResponseType.SUCCESS, "success");
        } else {
            return new EconomyResponse(0, 0, ResponseType.FAILURE,
                    "Could not withdraw the amount; Player data does not exist");
        }
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
        return withdrawPlayer(this.playersDataManager.getPlayerModel(playerName), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
        return withdrawPlayer(playerName, amount);
    }

    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to withdraw from
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(final OfflinePlayer player, final double amount) {
        return withdrawPlayer(this.playersDataManager.getPlayerModel(player), amount);
    }

    /**
     * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE
     * AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the
     * global balance will be returned.
     * 
     * @param player    to withdraw from
     * @param worldName - name of the world
     * @param amount    Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(final OfflinePlayer player, final String worldName, final double amount) {
        return withdrawPlayer(player, amount);
    }

    // ------------------------------[ PlayerAccount ]------------------------------
    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(final String playerName) {
        return this.playersDataManager.getPlayerModel(playerName) != null;
    }

    /**
     * Attempts to create a player account for the given player
     * 
     * @param player OfflinePlayer
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(final OfflinePlayer player) {
        return this.playersDataManager.getPlayerModel(player) != null;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(final String playerName, final String worldName) {
        return createPlayerAccount(playerName);
    }

    /**
     * Attempts to create a player account for the given player on the specified
     * world
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this then
     * false will always be returned.
     * 
     * @param player    OfflinePlayer
     * @param worldName String name of the world
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(final OfflinePlayer player, final String worldName) {
        return createPlayerAccount(player);
    }

    /**
     * 
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean hasAccount(final String playerName) {
        return this.playersDataManager.getPlayerModel(playerName) != null;
    }

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least
     * once
     * as all major economy plugins auto-generate a player account when the player
     * joins the server
     * 
     * @param player to check
     * @return if the player has an account
     */
    public boolean hasAccount(final OfflinePlayer player) {
        return this.playersDataManager.getPlayerModel(player) != null;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean hasAccount(final String playerName, final String worldName) {
        return hasAccount(playerName);
    }

    /**
     * Checks if this player has an account on the server yet on the given world
     * This will always return true if the player has joined the server at least
     * once
     * as all major economy plugins auto-generate a player account when the player
     * joins the server
     * 
     * @param player    to check in the world
     * @param worldName world-specific account
     * @return if the player has an account
     */
    public boolean hasAccount(final OfflinePlayer player, final String worldName) {
        return hasAccount(player);
    }

    // ------------------------------[ BankSupport ]------------------------------
    /**
     * Returns true if the given implementation supports banks.
     * 
     * @return true if the implementation supports banks
     */
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    /**
     * Gets the list of banks
     * 
     * @return the List of Banks
     */
    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    /**
     * Deletes a bank account with the specified name.
     * 
     * @param name of the back to delete
     * @return if the operation completed successfully
     */
    @Override
    public EconomyResponse deleteBank(final String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Returns the amount the bank has
     * 
     * @param name of the account
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankBalance(final String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name   of the account
     * @param amount to deposit
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankDeposit(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE
     * NEGATIVE AMOUNTS
     * 
     * @param name   of the account
     * @param amount to check for
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankHas(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name   of the account
     * @param amount to withdraw
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankWithdraw(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createBank(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse createBank(final String name, final String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Creates a bank account with the specified name and the player as the owner
     * 
     * @param name   of account
     * @param player the account should be linked to
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse createBank(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse isBankMember(final String name, final String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Check if the player is a member of the bank account
     * 
     * @param name   of the account
     * @param player to check membership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankMember(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse isBankOwner(final String name, final String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

    /**
     * Check if a player is the owner of a bank account
     * 
     * @param name   of the account
     * @param player to check for ownership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankOwner(final String name, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
    }

}
