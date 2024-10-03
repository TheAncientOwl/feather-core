/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file EcoCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Manage server economy
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.util.Pair;
import mc.owls.valley.net.feathercore.api.common.util.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.milkbowl.vault.economy.Economy;

public class EcoCommand extends FeatherCommand<EcoCommand.CommandData> {
    private static enum CommandType {
        SET, GIVE, TAKE,
    }

    public static record CommandData(OfflinePlayer player, CommandType commandType, double oldBalance, double amount) {
    }

    private Economy economy = null;
    private TranslationManager lang = null;
    private IPropertyAccessor economyConfig = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.economy = core.getEconomy();
        this.economyConfig = core.getFeatherEconomy().getConfig();
        this.lang = core.getTranslationManager();
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (data.commandType) {
            case GIVE:
                this.economy.depositPlayer(data.player, data.amount);
                break;
            case TAKE:
                this.economy.withdrawPlayer(data.player, data.amount);
                break;
            case SET:
                this.economy.withdrawPlayer(data.player, data.oldBalance);
                this.economy.depositPlayer(data.player, data.amount);
                break;
        }

        this.lang.message(sender, Message.ECO_SUCCESS,
                Pair.of(Placeholder.PLAYER, data.player.getName()),
                Pair.of(Placeholder.OLD, this.economy.format(data.oldBalance)),
                Pair.of(Placeholder.NEW, this.economy.format(this.economy.getBalance(data.player))));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check the basics
        if (!sender.hasPermission("feathercore.economy.setup.eco")) {
            this.lang.message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (args.length != 3) {
            this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_ECO);
            return null;
        }

        final String actionStr = args[0].toLowerCase();
        final String playerName = args[1];
        final String amountStr = args[2];

        // 2. get the player
        final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (!player.hasPlayedBefore()) {
            this.lang.message(sender, Message.NOT_PLAYER, Pair.of(Placeholder.STRING, playerName));
            return null;
        }

        // 3. get the amount
        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (final Exception e) {
            this.lang.message(sender, Message.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, amountStr));
            return null;
        }

        if (amount < 0 && (actionStr.equals("give") || actionStr.equals("take"))) {
            this.lang.message(sender, Message.ECO_NO_NEGATIVE_AMOUNT, Pair.of(Placeholder.STRING, actionStr));
            return null;
        }

        // 4. parse command type and validate the ranges
        final double oldBalance = this.economy.getBalance(player);
        CommandType commandType = null;
        switch (actionStr) {
            case "give": {
                final var max = this.economyConfig.getDouble("balance.max");
                if (oldBalance + amount > max) {
                    this.lang.message(sender, Message.ECO_BOUNDS_MAX,
                            Pair.of(Placeholder.MAX, this.economy.format(max)));
                    return null;
                }
                commandType = CommandType.GIVE;
                break;
            }
            case "take": {
                final var min = this.economyConfig.getDouble("balance.min");
                if (oldBalance - amount < min) {
                    this.lang.message(sender, Message.ECO_BOUNDS_MIN,
                            Pair.of(Placeholder.MIN, this.economy.format(min)));
                    return null;
                }
                commandType = CommandType.TAKE;
                break;
            }
            case "set": {
                final var max = this.economyConfig.getDouble("balance.max");
                if (amount > max) {
                    this.lang.message(sender, Message.ECO_BOUNDS_MAX,
                            Pair.of(Placeholder.MAX, this.economy.format(max)));
                    return null;
                }

                final var min = this.economyConfig.getDouble("balance.min");
                if (amount < min) {
                    this.lang.message(sender, Message.ECO_BOUNDS_MIN,
                            Pair.of(Placeholder.MIN, this.economy.format(min)));
                    return null;
                }

                commandType = CommandType.SET;
                break;
            }
            default: {
                this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_ECO);
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
