package mc.owls.valley.net.feathercore.modules.economy.common;

public class Message {
    public static final String BALANCE_SELF = "balance.self";
    public static final String BALANCE_OTHER = "balance.other";

    public static final String COMMAND_SENDER_NOT_PLAYER = "command-sender-not-player";
    public static final String PERMISSION_DENIED = "permission-denied";

    public static final String USAGE_INVALID = "usage-invalid";
    public static final String USAGE_BALANCE = "balance.usage";
    public static final String USAGE_ECO = "eco.usage";
    public static final String USAGE_PAY = "pay.error.usage";
    public static final String USAGE_PAYTOGGLE = "paytoggle.error.usage";
    public static final String USAGE_WITHDRAW = "withdraw.error.usage";
    public static final String USAGE_DEPOSIT = "deposit.error.usage";

    public static final String NOT_PLAYER = "not-player";
    public static final String NOT_ONLINE_PLAYER = "not-online-player";
    public static final String NOT_VALID_NUMBER = "not-valid-number";

    public static final String ECO_NO_NEGATIVE_AMOUNT = "eco.cannot-negative-amounts";
    public static final String ECO_SUCCESS = "eco.success";
    public static final String ECO_BOUNDS_MAX = "eco.bounds.max";
    public static final String ECO_BOUNDS_MIN = "eco.bounds.min";

    public static final String PAY_SELF = "pay.error.self";
    public static final String PAY_MIN_AMOUNT = "pay.error.min-amount";
    public static final String PAY_NO_FUNDS = "pay.error.not-enough-funds";
    public static final String PAY_BALANCE_EXCEEDS = "pay.error.balance-exceeds";
    public static final String PAY_SEND = "pay.success.send";
    public static final String PAY_RECEIVE = "pay.success.receive";

    public static final String PAY_TOGGLE_NOT_ACCEPTING = "paytoggle.error.does-not-accept";
    public static final String PAY_TOGGLE_TRUE = "paytoggle.success.status-true";
    public static final String PAY_TOGGLE_FALSE = "paytoggle.success.status-false";

    public static final String WITHDRAW_MIN_AMOUNT = "withdraw.error.minimum-value";
    public static final String WITHDRAW_NO_FUNDS = "withdraw.error.not-enough-funds";
    public static final String WITHDRAW_NO_SPACE = "withdraw.error.not-enough-space";
    public static final String WITHDRAW_SUCCESS = "withdraw.success";

    public static final String DEPOSIT_SUCCESS = "deposit.success";
    public static final String DEPOSIT_INVALID_AMOUNT = "deposit.error.invalid-amount";
    public static final String DEPOSIT_BALANCE_EXCEEDS = "deposit.error.balance-exceeds";
    public static final String DEPOSIT_NEGATIVE_AMOUNT = "deposit.error.negative-amount";

    public static final String BANKNOTE_METADATA_KEY = "feathercore-banknote-value";

    public static final String BANKNOTE_NAME = "banknote.display-name";
    public static final String BANKNOTE_LORE = "banknote.lore";
    public static final String BANKNOTE_INVALID_MATERIAL = "banknote.invalid-material";
    public static final String BANKNOTE_PLACE = "banknote.error.place";
    public static final String BANKNOTE_INVALID = "banknote.error.invalid";
}
