/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAcceptCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Accept a teleport request
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feather.toolkit.api.FeatherCommand;
import dev.defaultybuf.feather.toolkit.util.java.Clock;
import dev.defaultybuf.feather.toolkit.util.java.Pair;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;
import dev.defaultybuf.feather.toolkit.util.java.TimeUtils;
import dev.defaultybuf.feather.toolkit.util.parsing.Args;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class TeleportAcceptCommand extends FeatherCommand<TeleportAcceptCommand.CommandData> {
    public TeleportAcceptCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player issuer, Player target) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.request.accept")) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var requestStatus =
                getInterface(ITeleport.class).acceptRequest(data.issuer, data.target);
        switch (requestStatus) {
            case NO_SUCH_REQUEST: {
                getLanguage().message(sender, Message.Teleport.NO_SUCH_REQUEST);
                break;
            }
            case ACCEPTED: {
                getLanguage().message(data.issuer, Message.Teleport.REQUEST_ACCEPT_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                getLanguage().message(data.target, Message.Teleport.REQUEST_ACCEPT_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));

                final var delay =
                        getInterface(ITeleport.class).getConfig().getMillis("request.accept-delay");
                if (delay > 0) {
                    getLanguage().message(data.target, Message.Teleport.REQUEST_DELAY,
                            Pair.of(Placeholder.COOLDOWN,
                                    TimeUtils.formatRemaining(Clock.currentTimeMillis(), delay)));
                }

                break;
            }
            default: {
                assert false : "[modules.teleport.commands]@TeleportAcceptCommand.execute(CommandSender, CommandData): branch not handled for '"
                        + requestStatus.toString() + "'";
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player issuer = null;
        Player target = null;

        switch (args.length) {
            case 1: {
                // /tpaccept issuer-player
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        getLanguage().message(sender, Message.General.PLAYERS_ONLY);
                        return null;
                    }

                    issuer = parsedArgs.getPlayer(0);
                    target = (Player) sender;
                } else {
                    getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_REQUEST_ACCEPT);
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
