/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportRequestCommand.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Request teleport to a player
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
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport.RequestType;
import mc.owls.valley.net.feathercore.modules.teleport.interfaces.ITeleport;

@SuppressWarnings("unchecked")
public class TeleportRequestCommand extends FeatherCommand<TeleportRequestCommand.CommandData> {
    public TeleportRequestCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player issuer, Player target) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.request.to")) {
            getInterface(ILanguage.class).message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (getInterface(ITeleport.class).request(data.issuer, data.target, RequestType.TO)) {
            case ALREADY_REQUESTED: {
                getInterface(ILanguage.class).message(data.issuer, Message.Teleport.REQUEST_TO_EXECUTE_PENDING,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                break;
            }
            case REQUESTED: {
                getInterface(ILanguage.class).message(data.issuer, Message.Teleport.REQUEST_TO_EXECUTE_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                getInterface(ILanguage.class).message(data.target, Message.Teleport.REQUEST_TO_EXECUTE_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));
                break;
            }
            default: {
                throw new Error(
                        "Logic error: TeleportRequestCommand.java::execute(CommandSender, CommandData). Please notify developer");
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
                        getInterface(ILanguage.class).message(sender, Message.General.PLAYERS_ONLY);
                        return null;
                    }

                    issuer = (Player) sender;
                    target = parsedArgs.getPlayer(0);
                } else {
                    getInterface(ILanguage.class).message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID,
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
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            default:
                break;
        }

        return completions;
    }

}
