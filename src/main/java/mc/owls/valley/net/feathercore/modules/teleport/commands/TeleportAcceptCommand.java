/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAcceptCommand.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Accept a teleport request
 */

package mc.owls.valley.net.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Args;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.common.util.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.teleport.interfaces.ITeleport;

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
        switch (getInterface(ITeleport.class).acceptRequest(data.issuer, data.target)) {
            case NO_SUCH_REQUEST: {
                getLanguage().message(sender, Message.Teleport.NO_SUCH_REQUEST);
                break;
            }
            case ACCEPTED: {
                getLanguage().message(data.issuer, Message.Teleport.REQUEST_ACCEPT_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                getLanguage().message(data.target, Message.Teleport.REQUEST_ACCEPT_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));

                final var delay = getInterface(ITeleport.class).getConfig().getMillis("request.accept-delay");
                if (delay > 0) {
                    getLanguage().message(data.target, Message.Teleport.REQUEST_DELAY,
                            Pair.of(Placeholder.COOLDOWN,
                                    TimeUtils.formatRemaining(System.currentTimeMillis(), delay)));
                }

                break;
            }
            default: {
                throw new Error(
                        "Logic error: TeleportAcceptCommand.java::execute(CommandSender, CommandData). Please notify developer");
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
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            default:
                break;
        }

        return completions;
    }

}
