/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PayCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Pay player with in-game currency
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.core.FeatherCommand;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

public class PayCommand extends FeatherCommand<PayCommand.CommandData> {
    public PayCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(OfflinePlayer receiver, double amount) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.pay")) {
            getLanguage().message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();

        economy.withdrawPlayer((Player) sender, data.amount);
        economy.depositPlayer(data.receiver, data.amount);

        final var amount = economy.format(data.amount);

        getLanguage().message(sender, Message.Economy.PAY_SEND, List.of(
                Pair.of(Placeholder.PLAYER, data.receiver.getName()),
                Pair.of(Placeholder.AMOUNT, amount)));
        getLanguage().message((Player) data.receiver, Message.Economy.PAY_RECEIVE, List.of(
                Pair.of(Placeholder.PLAYER, ((Player) sender).getName()),
                Pair.of(Placeholder.AMOUNT, amount)));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check the basics
        if (!(sender instanceof Player)) {
            getLanguage().message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        if (args.length != 2) {
            getLanguage().message(sender, Message.General.USAGE_INVALID, Message.Economy.USAGE_PAY);
            return null;
        }

        // 3. check if receiver is player
        final OfflinePlayer receiverPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!receiverPlayer.hasPlayedBefore()) {
            getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        if (!receiverPlayer.isOnline()) {
            getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                    Pair.of(Placeholder.PLAYER, receiverPlayer.getName()));
            return null;
        }

        // 4. check if receiver accepts payments
        final PlayerModel playerModel =
                getInterface(IPlayersData.class).getPlayerModel(receiverPlayer);
        if (playerModel == null) {
            return null;
        }

        if (!playerModel.acceptsPayments
                && !sender.hasPermission("feathercore.economy.general.pay.override")) {
            getLanguage().message(sender, Message.Economy.PAY_TOGGLE_NOT_ACCEPTING,
                    Pair.of(Placeholder.PLAYER, receiverPlayer.getName()));
            return null;
        }

        // 5. parse amount
        double amount = 0;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (final Exception e) {
            getLanguage().message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return null;
        }

        // 6. check if amount is viable to be transferred
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();
        final var economyConfig = getInterface(IFeatherEconomy.class).getConfig();

        final var minAmount = economyConfig.getDouble("minimum-pay-amount");
        if (amount < minAmount) {
            getLanguage().message(sender, Message.Economy.PAY_MIN_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, minAmount));
            return null;
        }

        if (!economy.has((Player) sender, amount)) {
            getLanguage().message(sender, Message.Economy.PAY_NO_FUNDS);
            return null;
        }

        final var maxBalance = economyConfig.getDouble("balance.max");
        if (economy.getBalance(receiverPlayer) + amount > maxBalance) {
            getLanguage().message(sender, Message.Economy.PAY_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, maxBalance));
            return null;
        }

        return new CommandData(receiverPlayer, amount);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final var arg = args[0];
            final List<String> onlinePlayers = StringUtils.getOnlinePlayers();

            if (arg.isEmpty()) {
                completions = onlinePlayers;
            } else {
                completions = StringUtils.filterStartingWith(onlinePlayers, arg);
            }
        } else if (args.length == 2) {
            completions.add("amount");
        }

        return completions;
    }
}
