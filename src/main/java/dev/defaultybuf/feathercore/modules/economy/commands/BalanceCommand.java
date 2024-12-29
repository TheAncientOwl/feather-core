/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BalanceCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Check player's balance
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
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

public class BalanceCommand extends FeatherCommand<BalanceCommand.CommandData> {
    public BalanceCommand(final InitData data) {
        super(data);
    }

    private static enum CommandType {
        SELF, OTHER
    }

    public static record CommandData(CommandType commandType, OfflinePlayer other) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.balance")) {
            getLanguage().message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();
        switch (data.commandType) {
            case SELF:
                getLanguage().message(sender, Message.Economy.BALANCE_SELF,
                        Pair.of(Placeholder.BALANCE,
                                economy.format(economy.getBalance((Player) sender))));
                break;
            case OTHER:
                getLanguage().message(sender, Message.Economy.BALANCE_OTHER, List.of(
                        Pair.of(Placeholder.PLAYER, data.other.getName()),
                        Pair.of(Placeholder.BALANCE,
                                economy.format(economy.getBalance(data.other)))));
                break;
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        CommandType commandType = null;
        OfflinePlayer targetPlayer = null;

        if (args.length != 0) {
            if (args.length != 1) {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Economy.USAGE_BALANCE);
                return null;
            }

            commandType = CommandType.OTHER;
            targetPlayer = Bukkit.getOfflinePlayer(args[0]);

            if (!targetPlayer.hasPlayedBefore()) {
                getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                        Pair.of(Placeholder.STRING, args[0]));
                return null;
            }
        } else if (sender instanceof Player) {
            commandType = CommandType.SELF;
        } else {
            getLanguage().message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        return new CommandData(commandType, targetPlayer);
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
        }

        return completions;
    }

}
