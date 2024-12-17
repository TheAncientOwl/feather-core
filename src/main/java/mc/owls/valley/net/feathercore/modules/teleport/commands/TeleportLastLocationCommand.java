/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportLastPositionCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Teleport to the last known location of the player
 */

package mc.owls.valley.net.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Args;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LocationModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.teleport.interfaces.ITeleport;

public class TeleportLastLocationCommand extends FeatherCommand<TeleportLastLocationCommand.CommandData> {
    public TeleportLastLocationCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player who, LocationModel destination) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        if (!sender.hasPermission("feathercore.teleport.lastknown") ||
                (!selfTeleport && !sender.hasPermission("feathercore.teleport.lastknown.other"))) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        final var world = Bukkit.getWorld(data.destination.world);
        if (world == null) {
            getLanguage().message(sender, Message.General.WORLD_NO_LONGER_AVAILABLE,
                    Pair.of(Placeholder.WORLD, data.destination.world));
            return;
        }

        getInterface(ITeleport.class).teleport(data.who, data.destination.x, data.destination.y, data.destination.z,
                world);

        getLanguage().message(data.who, Message.Teleport.POSITION, List.of(
                Pair.of(Placeholder.X, (int) data.destination.x),
                Pair.of(Placeholder.Y, (int) data.destination.y),
                Pair.of(Placeholder.Z, (int) data.destination.z),
                Pair.of(Placeholder.WORLD, data.destination.world)));

        if (!selfTeleport) {
            getLanguage().message(sender, Message.Teleport.POSITION_OTHER, List.of(
                    Pair.of(Placeholder.PLAYER, data.who.getName()),
                    Pair.of(Placeholder.X, (int) data.destination.x),
                    Pair.of(Placeholder.Y, (int) data.destination.y),
                    Pair.of(Placeholder.Z, (int) data.destination.z),
                    Pair.of(Placeholder.WORLD, data.destination.world)));
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        PlayerModel destination = null;
        Player who = null;

        switch (args.length) {
            case 1: {
                // /tpoffline [destination-player]
                final var parsedArgs = Args.parse(args, Args::getOfflinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        getLanguage().message(sender, Message.General.PLAYERS_ONLY);
                        return null;
                    } else {
                        who = (Player) sender;
                        destination = getInterface(IPlayersData.class).getPlayerModel(parsedArgs.getOfflinePlayer(0));
                    }
                } else {
                    getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                            Pair.of(Placeholder.STRING, args[0]));
                    return null;
                }
                break;
            }
            case 2: {
                // /tpoffline [destination-player] (who-player)
                final var parsedArgs = Args.parse(args, Args::getOfflinePlayer, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    destination = getInterface(IPlayersData.class).getPlayerModel(parsedArgs.getOfflinePlayer(0));
                    who = parsedArgs.getPlayer(1);
                    if (who == null) {
                        return null;
                    }
                } else {
                    switch (parsedArgs.failIndex()) {
                        case 0: {
                            getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                                    Pair.of(Placeholder.STRING, args[0]));
                            break;
                        }
                        case 1: {
                            getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                                    Pair.of(Placeholder.PLAYER, args[1]));
                            break;
                        }
                    }
                    return null;
                }
                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_OFFLINE);
                return null;
            }
        }

        if (destination == null) {
            getLanguage().message(sender, Message.General.NOT_VALID_PLAYER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        return new CommandData(who, destination.lastKnownLocation);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions.add("player");
                break;
            case 2:
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[1]);
                break;
        }

        return completions;
    }

}
