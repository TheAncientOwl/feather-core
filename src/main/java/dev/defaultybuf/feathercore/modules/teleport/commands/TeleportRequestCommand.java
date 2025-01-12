/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportRequestCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Request teleport to a player
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
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestType;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class TeleportRequestCommand extends FeatherCommand<TeleportRequestCommand.CommandData> {
    public TeleportRequestCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player issuer, Player target) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.request.to")) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var requestStatus =
                getInterface(ITeleport.class).request(data.issuer, data.target, RequestType.TO);
        switch (requestStatus) {
            case ALREADY_REQUESTED: {
                getLanguage().message(data.issuer, Message.Teleport.REQUEST_TO_EXECUTE_PENDING,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                break;
            }
            case REQUESTED: {
                getLanguage().message(data.issuer, Message.Teleport.REQUEST_TO_EXECUTE_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                getLanguage().message(data.target, Message.Teleport.REQUEST_TO_EXECUTE_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));
                break;
            }
            default: {
                assert false : "[modules.teleport.commands]@TeleportRequestCommand.execute(CommandSender, CommandData): branch not handled for '"
                        + requestStatus.toString() + "'";
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player issuer = null;
        Player target = null;

        switch (args.length) {
            case 1: {
                // /tpa target-player
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
                        Message.Teleport.USAGE_REQUEST_TO);
                return null;
            }
        }

        return new CommandData(issuer, target);
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
