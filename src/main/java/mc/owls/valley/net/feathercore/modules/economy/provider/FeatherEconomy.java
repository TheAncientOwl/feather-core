package mc.owls.valley.net.feathercore.modules.economy.provider;

import java.util.List;

import org.bukkit.OfflinePlayer;

import mc.owls.valley.net.feathercore.modules.data.players.manager.api.IPlayersDataManager;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;

public class FeatherEconomy extends AbstractEconomy {
    private IPlayersDataManager playersDataManager = null;

    public FeatherEconomy(final IPlayersDataManager playersDataManager) {
        this.playersDataManager = playersDataManager;
    }

    /**
     * Returns the amount the bank has
     * 
     * @param name of the account
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankBalance(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankBalance'");
    }

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name   of the account
     * @param amount to deposit
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankDeposit'");
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
    public EconomyResponse bankHas(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankHas'");
    }

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param name   of the account
     * @param amount to withdraw
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankWithdraw'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createBank(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse createBank(String name, String player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    /**
     * Creates a bank account with the specified name and the player as the owner
     * 
     * @param name   of account
     * @param player the account should be linked to
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    /**
     * Attempts to create a player account for the given player
     * 
     * @param player OfflinePlayer
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
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
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'currencyNamePlural'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'currencyNameSingular'");
    }

    /**
     * Deletes a bank account with the specified name.
     * 
     * @param name of the back to delete
     * @return if the operation completed successfully
     */
    @Override
    public EconomyResponse deleteBank(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBank'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to deposit to
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
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
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    @Override
    public String format(double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'format'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fractionalDigits'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)}
     *             instead.
     */
    @Override
    @Deprecated
    public double getBalance(String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }

    /**
     * Gets balance of a player
     * 
     * @param player of the player
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)}
     *             instead.
     */
    @Override
    @Deprecated
    public double getBalance(String playerName, String world) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
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
    public double getBalance(OfflinePlayer player, String world) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }

    /**
     * Gets the list of banks
     * 
     * @return the List of Banks
     */
    @Override
    public List<String> getBanks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBanks'");
    }

    /**
     * Gets name of economy method
     * 
     * @return Name of Economy Method
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use @{link
     *             {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
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
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
    }

    /**
     * 
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
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
    public boolean hasAccount(OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)}
     *             instead.
     */
    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
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
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
    }

    /**
     * Returns true if the given implementation supports banks.
     * 
     * @return true if the implementation supports banks
     */
    @Override
    public boolean hasBankSupport() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasBankSupport'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    /**
     * Check if the player is a member of the bank account
     * 
     * @param name   of the account
     * @param player to check membership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {{@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    /**
     * Check if a player is the owner of a bank account
     * 
     * @param name   of the account
     * @param player to check for ownership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    /**
     * Checks if economy method is enabled.
     * 
     * @return Success or Failure
     */
    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEnabled'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
    }

    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     * 
     * @param player to withdraw from
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use
     *             {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
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
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
    }

}
