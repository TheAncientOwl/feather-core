/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file EcoCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Manage server economy
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomy;

public class EcoCommand extends FeatherCommand<EcoCommand.CommandData> {
    public EcoCommand(final InitData data) {
        super(data);
    }

    private static enum CommandType {
        SET, GIVE, TAKE,
    }

    public static record CommandData(OfflinePlayer player, CommandType commandType,
            double oldBalance, double amount) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.setup.eco")) {
            getLanguage().message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();

        switch (data.commandType) {
            case GIVE:
                economy.depositPlayer(data.player, data.amount);
                break;
            case TAKE:
                economy.withdrawPlayer(data.player, data.amount);
                break;
            case SET:
                economy.withdrawPlayer(data.player, data.oldBalance);
                economy.depositPlayer(data.player, data.amount);
                break;
        }

        getLanguage().message(sender, Message.Economy.ECO_SUCCESS, List.of(
                Pair.of(Placeholder.PLAYER, data.player.getName()),
                Pair.of(Placeholder.OLD, economy.format(data.oldBalance)),
                Pair.of(Placeholder.NEW, economy.format(economy.getBalance(data.player)))));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check the basics
        if (args.length != 3) {
            getLanguage().message(sender, Message.General.USAGE_INVALID, Message.Economy.USAGE_ECO);
            return null;
        }

        final String actionStr = args[0].toLowerCase();
        final String playerName = args[1];
        final String amountStr = args[2];

        // 2. get the player
        final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (!player.hasPlayedBefore()) {
            getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                    Pair.of(Placeholder.STRING, playerName));
            return null;
        }

        // 3. get the amount
        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (final Exception e) {
            getLanguage().message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, amountStr));
            return null;
        }

        if (amount < 0 && (actionStr.equals("give") || actionStr.equals("take"))) {
            getLanguage().message(sender, Message.Economy.ECO_NO_NEGATIVE_AMOUNT,
                    Pair.of(Placeholder.STRING, actionStr));
            return null;
        }

        // 4. parse command type and validate the ranges
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();
        final var economyConfig = getInterface(IFeatherEconomy.class).getConfig();

        final double oldBalance = economy.getBalance(player);
        CommandType commandType = null;
        switch (actionStr) {
            case "give": {
                final var max = economyConfig.getDouble("balance.max");
                if (oldBalance + amount > max) {
                    getLanguage().message(sender, Message.Economy.ECO_BOUNDS_MAX,
                            Pair.of(Placeholder.MAX, economy.format(max)));
                    return null;
                }
                commandType = CommandType.GIVE;
                break;
            }
            case "take": {
                final var min = economyConfig.getDouble("balance.min");
                if (oldBalance - amount < min) {
                    getLanguage().message(sender, Message.Economy.ECO_BOUNDS_MIN,
                            Pair.of(Placeholder.MIN, economy.format(min)));
                    return null;
                }
                commandType = CommandType.TAKE;
                break;
            }
            case "set": {
                final var max = economyConfig.getDouble("balance.max");
                if (amount > max) {
                    getLanguage().message(sender, Message.Economy.ECO_BOUNDS_MAX,
                            Pair.of(Placeholder.MAX, economy.format(max)));
                    return null;
                }

                final var min = economyConfig.getDouble("balance.min");
                if (amount < min) {
                    getLanguage().message(sender, Message.Economy.ECO_BOUNDS_MIN,
                            Pair.of(Placeholder.MIN, economy.format(min)));
                    return null;
                }

                commandType = CommandType.SET;
                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Economy.USAGE_ECO);
                return null;
            }
        }

        return new CommandData(player, commandType, oldBalance, amount);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final var arg = args[0].toLowerCase();
            if (arg.startsWith("s")) {
                completions.add("set");
            } else if (arg.startsWith("g")) {
                completions.add("give");
            } else if (arg.startsWith("t")) {
                completions.add("take");
            } else {
                completions.add("set");
                completions.add("give");
                completions.add("take");
            }
        } else if (args.length == 2) {
            final var arg = args[1];
            final List<String> onlinePlayers = StringUtils.getOnlinePlayers();

            if (arg.isEmpty()) {
                completions = onlinePlayers;
            } else {
                completions = StringUtils.filterStartingWith(onlinePlayers, arg);
            }

        } else if (args.length == 3) {
            completions.add("amount");
        }

        return completions;
    }

}
