/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportRequestCancelCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Cancel a sent teleport request
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.Args;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.core.FeatherCommand;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class TeleportRequestCancelCommand
        extends FeatherCommand<TeleportRequestCancelCommand.CommandData> {
    public TeleportRequestCancelCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player issuer, Player target) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.request.cancel")) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var requestStatus =
                getInterface(ITeleport.class).cancelRequest(data.issuer, data.target);
        switch (requestStatus) {
            case NO_SUCH_REQUEST: {
                getLanguage().message(sender, Message.Teleport.NO_SUCH_REQUEST);
                break;
            }
            case CANCELLED: {
                getLanguage().message(data.issuer, Message.Teleport.REQUEST_CANCEL,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                break;
            }
            default: {
                assert false : "[modules.teleport.commands]@TeleportRequestCancelCommand.execute(CommandSender, CommandData): branch not handled for '"
                        + requestStatus.toString() + "'";
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player issuer = null;
        Player target = null;

        switch (args.length) {
            case 1: {
                // /tpacancel issuer-player
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        getLanguage().message(sender, Message.General.PLAYERS_ONLY);
                        return null;
                    }

                    issuer = (Player) sender;
                    target = parsedArgs.getPlayer(0);
                } else {
                    getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_REQUEST_CANCEL);
                return null;
            }
        }

        return new CommandData(issuer, target);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        // TODO: Tab complete only available requests
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
