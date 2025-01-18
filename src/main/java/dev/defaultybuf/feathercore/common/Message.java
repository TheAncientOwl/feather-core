/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Message.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Message keys from language yml files
 */

package dev.defaultybuf.feathercore.common;

public final class Message {
    public static final class General {
        public static final String PERMISSION_DENIED = "general.command.no-permission";
        public static final String USAGE_INVALID = "general.command.invalid";
        public static final String NO_PERMISSION = "general.no-permission";
        public static final String PLAYERS_ONLY = "general.command.players-only";
        public static final String NAN = "general.not-valid-number";
        public static final String NOT_ONLINE_PLAYER = "general.not-online-player";
        public static final String NOT_VALID_PLAYER = "general.not-player";
        public static final String NOT_VALID_WORLD = "general.not-valid-world";
        public static final String NOT_VALID_VALUE = "general.invalid-value";
        public static final String NOT_VALID_NUMBER = "general.not-valid-number";
        public static final String WORLD_NO_LONGER_AVAILABLE = "general.world-no-longer-available";
    }

    public static final class Reload {
        public static final String USAGE = "configuration.usage";
        public static final String CONFIG_RELOADED = "configuration.reload.single";
        public static final String CONFIGS_RELOADED = "configuration.reload.multiple";
    }

    public static final class Language {
        public static final String USAGE = "language.usage";
        public static final String CHANGE_SUCCESS = "language.change-success";
        public static final String UNKNOWN = "language.unknown";
        public static final String INFO = "language.info";
        public static final String LIST = "language.list";
    }

    public static final class PvPManager {
        public static final String TAG = "pvp.combat.tag";
        public static final String TAGGED = "pvp.combat.tagged";
        public static final String COMBAT_ENDED = "pvp.combat.ended";
        public static final String BLOCK_COMMAND = "pvp.block-command";
        public static final String LOGOUT = "pvp.logout";
        public static final String TELEPORT = "pvp.teleport";
    }

    public static final class Economy {
        public static final String BALANCE_SELF = "economy.balance.self";
        public static final String BALANCE_OTHER = "economy.balance.other";

        public static final String USAGE_BALANCE = "economy.balance.usage";
        public static final String USAGE_ECO = "economy.eco.usage";
        public static final String USAGE_PAY = "economy.pay.error.usage";
        public static final String USAGE_PAYTOGGLE = "economy.paytoggle.error.usage";
        public static final String USAGE_WITHDRAW = "economy.withdraw.error.usage";
        public static final String USAGE_DEPOSIT = "economy.deposit.error.usage";

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

        public static final String PAY_TOGGLE_NOT_ACCEPTING =
                "economy.paytoggle.error.does-not-accept";
        public static final String PAY_TOGGLE_TRUE = "economy.paytoggle.success.status-true";
        public static final String PAY_TOGGLE_FALSE = "economy.paytoggle.success.status-false";

        public static final String WITHDRAW_MIN_AMOUNT = "economy.withdraw.error.minimum-value";
        public static final String WITHDRAW_NO_FUNDS = "economy.withdraw.error.not-enough-funds";
        public static final String WITHDRAW_NO_SPACE = "economy.withdraw.error.not-enough-space";
        public static final String WITHDRAW_SUCCESS = "economy.withdraw.success";

        public static final String DEPOSIT_SUCCESS = "economy.deposit.success";
        public static final String DEPOSIT_INVALID_AMOUNT = "economy.deposit.error.invalid-amount";
        public static final String DEPOSIT_BALANCE_EXCEEDS =
                "economy.deposit.error.balance-exceeds";
        public static final String DEPOSIT_NEGATIVE_AMOUNT =
                "economy.deposit.error.negative-amount";

        public static final String BANKNOTE_LANG = "economy.banknote.lang";
        public static final String BANKNOTE_NAME = "economy.banknote.display-name";
        public static final String BANKNOTE_LORE = "economy.banknote.lore";
        public static final String BANKNOTE_INVALID_MATERIAL = "economy.banknote.invalid-material";
        public static final String BANKNOTE_PLACE = "economy.banknote.error.place";
        public static final String BANKNOTE_INVALID = "economy.banknote.error.invalid";
    }

    public static final class LootChests {
        public static final String NOT_A_CHEST = "loot-chests.not-a-chest";
        public static final String NOT_A_REGISTERED_CHEST = "loot-chests.not-a-registered-chest";
        public static final String SET_SUCCESS = "loot-chests.set-success";
        public static final String UNSET_SUCCESS = "loot-chests.unset-success";
        public static final String CREATE_SUCCESS = "loot-chests.create-success";
        public static final String DELETE_SUCCESS = "loot-chests.delete-success";
        public static final String INFO = "loot-chests.info";
        public static final String LOCATIONS = "loot-chests.locations";
        public static final String USAGE = "loot-chests.usage";
        public static final String NOT_CHEST_TYPE = "loot-chests.not-a-chest-type";
        public static final String COOLDOWN = "loot-chests.cooldown";
    }

    public static final class Teleport {
        public static final String USAGE_POSITION = "teleport.usage.position";
        public static final String USAGE_OFFLINE = "teleport.usage.offline";
        public static final String USAGE_PLAYER = "teleport.usage.player";
        public static final String USAGE_TPHERE = "teleport.usage.tphere";
        public static final String USAGE_TPALL = "teleport.usage.tpall";
        public static final String USAGE_RTP = "teleport.usage.rtp";
        public static final String USAGE_REQUEST_TO = "teleport.usage.request.to";
        public static final String USAGE_REQUEST_HERE = "teleport.usage.request.here";
        public static final String USAGE_REQUEST_ACCEPT = "teleport.usage.request.accept";
        public static final String USAGE_REQUEST_CANCEL = "teleport.usage.request.cancel";
        public static final String USAGE_REQUEST_DENY = "teleport.usage.request.deny";

        public static final String POSITION = "teleport.position";
        public static final String POSITION_OTHER = "teleport.position-other";
        public static final String PLAYER = "teleport.player";
        public static final String PLAYER_SELF = "teleport.player-self";
        public static final String HERE = "teleport.tphere";
        public static final String ALL_SELF = "teleport.tpall.self";
        public static final String ALL_OTHER = "teleport.tpall.other";
        public static final String RTP_SELF = "teleport.rtp.self";
        public static final String RTP_OTHER = "teleport.rtp.other";
        public static final String RTP_FAIL = "teleport.rtp.fail";
        public static final String RTP_COOLDOWN = "teleport.rtp.cooldown";
        public static final String RTP_TRY = "teleport.rtp.try";
        public static final String REQUEST_DELAY = "teleport.request.delay";
        public static final String REQUEST_ACCEPT_ISSUER = "teleport.request.accept.issuer";
        public static final String REQUEST_ACCEPT_TARGET = "teleport.request.accept.target";
        public static final String REQUEST_DECLINE_ISSUER = "teleport.request.decline.issuer";
        public static final String REQUEST_DECLINE_TARGET = "teleport.request.decline.target";
        public static final String REQUEST_TO_EXECUTE_PENDING =
                "teleport.request.execute.to.pending";
        public static final String REQUEST_TO_EXECUTE_ISSUER = "teleport.request.execute.to.issuer";
        public static final String REQUEST_TO_EXECUTE_TARGET = "teleport.request.execute.to.target";
        public static final String REQUEST_HERE_EXECUTE_PENDING =
                "teleport.request.execute.here.pending";
        public static final String REQUEST_HERE_EXECUTE_ISSUER =
                "teleport.request.execute.here.issuer";
        public static final String REQUEST_HERE_EXECUTE_TARGET =
                "teleport.request.execute.here.target";
        public static final String NO_SUCH_REQUEST = "teleport.request.no-such-request";
        public static final String REQUEST_CANCEL = "teleport.request.cancel";
        public static final String MOVED_WHILE_WAITING = "teleport.request.moved-while-waiting";
    }
}
