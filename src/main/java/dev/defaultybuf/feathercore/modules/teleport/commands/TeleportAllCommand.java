/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAllCommand.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @description Teleport all players to the command sender
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feather.toolkit.api.FeatherCommand;
import dev.defaultybuf.feather.toolkit.util.java.Pair;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feather.toolkit.util.parsing.Args;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class TeleportAllCommand extends FeatherCommand<TeleportAllCommand.CommandData> {
    public TeleportAllCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player where) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.all")) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        for (final var player : Bukkit.getOnlinePlayers()) {
            getInterface(ITeleport.class).teleport(player, data.where);
        }

        if (sender instanceof Player && data.where.equals((Player) sender)) {
            getLanguage().message(sender, Message.Teleport.ALL_SELF);
        } else {
            getLanguage().message(sender, Message.Teleport.ALL_OTHER,
                    Pair.of(Placeholder.TARGET, data.where.getName()));
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player where = null;

        switch (args.length) {
            case 0: {
                // /tpall
                if (!(sender instanceof Player)) {
                    getLanguage().message(sender, Message.General.PLAYERS_ONLY);
                    return null;
                }
                where = (Player) sender;
                break;
            }
            case 1: {
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    where = parsedArgs.getPlayer(0);
                } else {
                    getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_TPALL);
                return null;
            }
        }

        return new CommandData(where);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions =
                        StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            default:
                break;
        }

        return completions;
    }

}
