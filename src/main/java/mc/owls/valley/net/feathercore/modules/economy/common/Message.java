/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Message.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Module specific language messages
 */

package mc.owls.valley.net.feathercore.modules.economy.common;

public class Message {
    public static final String BALANCE_SELF = "economy.balance.self";
    public static final String BALANCE_OTHER = "economy.balance.other";

    public static final String COMMAND_SENDER_NOT_PLAYER = "general.command.players-only";
    public static final String PERMISSION_DENIED = "general.command.no-permission";

    public static final String USAGE_INVALID = "general.command.invalid";
    public static final String USAGE_BALANCE = "economy.balance.usage";
    public static final String USAGE_ECO = "economy.eco.usage";
    public static final String USAGE_PAY = "economy.pay.error.usage";
    public static final String USAGE_PAYTOGGLE = "economy.paytoggle.error.usage";
    public static final String USAGE_WITHDRAW = "economy.withdraw.error.usage";
    public static final String USAGE_DEPOSIT = "economy.deposit.error.usage";

    public static final String NOT_PLAYER = "general.not-player";
    public static final String NOT_ONLINE_PLAYER = "general.not-online-player";
    public static final String NOT_VALID_NUMBER = "general.not-valid-number";

    public static final String ECO_NO_NEGATIVE_AMOUNT = "economy.eco.cannot-negative-amounts";
    public static final String ECO_SUCCESS = "economy.eco.success";
    public static final String ECO_BOUNDS_MAX = "economy.eco.bounds.max";
    public static final String ECO_BOUNDS_MIN = "economy.eco.bounds.min";

    public static final String PAY_SELF = "economy.pay.error.self";
    public static final String PAY_MIN_AMOUNT = "economy.pay.error.min-amount";
    public static final String PAY_NO_FUNDS = "economy.pay.error.not-enough-funds";
    public static final String PAY_BALANCE_EXCEEDS = "economy.pay.error.balance-exceeds";
    public static final String PAY_SEND = "economy.pay.success.send";
    public static final String PAY_RECEIVE = "economy.pay.success.receive";

    public static final String PAY_TOGGLE_NOT_ACCEPTING = "economy.paytoggle.error.does-not-accept";
    public static final String PAY_TOGGLE_TRUE = "economy.paytoggle.success.status-true";
    public static final String PAY_TOGGLE_FALSE = "economy.paytoggle.success.status-false";

    public static final String WITHDRAW_MIN_AMOUNT = "economy.withdraw.error.minimum-value";
    public static final String WITHDRAW_NO_FUNDS = "economy.withdraw.error.not-enough-funds";
    public static final String WITHDRAW_NO_SPACE = "economy.withdraw.error.not-enough-space";
    public static final String WITHDRAW_SUCCESS = "economy.withdraw.success";

    public static final String DEPOSIT_SUCCESS = "economy.deposit.success";
    public static final String DEPOSIT_INVALID_AMOUNT = "economy.deposit.error.invalid-amount";
    public static final String DEPOSIT_BALANCE_EXCEEDS = "economy.deposit.error.balance-exceeds";
    public static final String DEPOSIT_NEGATIVE_AMOUNT = "economy.deposit.error.negative-amount";

    public static final String BANKNOTE_METADATA_KEY = "economy.feathercore-banknote-value";

    public static final String BANKNOTE_NAME = "economy.banknote.display-name";
    public static final String BANKNOTE_LORE = "economy.banknote.lore";
    public static final String BANKNOTE_INVALID_MATERIAL = "economy.banknote.invalid-material";
    public static final String BANKNOTE_PLACE = "economy.banknote.error.place";
    public static final String BANKNOTE_INVALID = "economy.banknote.error.invalid";
}
