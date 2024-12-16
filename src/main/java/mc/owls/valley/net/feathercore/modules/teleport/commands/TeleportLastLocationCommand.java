/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportLastPositionCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
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
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.LocationModel;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;
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
            getInterface(ILanguage.class).message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        final var world = Bukkit.getWorld(data.destination.world);
        if (world == null) {
            getInterface(ILanguage.class).message(sender, Message.General.WORLD_NO_LONGER_AVAILABLE,
                    Pair.of(Placeholder.WORLD, data.destination.world));
            return;
        }

        getInterface(ITeleport.class).teleport(data.who, data.destination.x, data.destination.y, data.destination.z,
                world);

        getInterface(ILanguage.class).message(data.who, Message.Teleport.POSITION, List.of(
                Pair.of(Placeholder.X, (int) data.destination.x),
                Pair.of(Placeholder.Y, (int) data.destination.y),
                Pair.of(Placeholder.Z, (int) data.destination.z),
                Pair.of(Placeholder.WORLD, data.destination.world)));

        if (!selfTeleport) {
            getInterface(ILanguage.class).message(sender, Message.Teleport.POSITION_OTHER, List.of(
                    Pair.of(Placeholder.PLAYER, data.who.getName()),
                    Pair.of(Placeholder.X, (int) data.destination.x),
                    Pair.of(Placeholder.Y, (int) data.destination.y),
                    Pair.of(Placeholder.Z, (int) data.destination.z),
                    Pair.of(Placeholder.WORLD, data.destination.world)));
        }
    }

    // TODO: Update with new parser approach
    protected CommandData parse(final CommandSender sender, final String[] args) {
        PlayerModel destination = null;
        Player who = null;

        switch (args.length) {
            // /tpoffline [destination-player]
            case 1: {
                destination = getInterface(IPlayersData.class).getPlayerModel(args[0]);

                if (!(sender instanceof Player)) {
                    getInterface(ILanguage.class).message(sender, Message.General.PLAYERS_ONLY);
                    return null;
                } else {
                    who = (Player) sender;
                }

                break;
            }
            // /tpoffline [destination-player] (who-player)
            case 2: {
                destination = getInterface(IPlayersData.class).getPlayerModel(args[0]);

                who = Bukkit.getPlayerExact(args[1]);
                if (who == null) {
                    getInterface(ILanguage.class).message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[1]));
                    return null;
                }

                break;
            }
            default: {
                getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_OFFLINE);
                return null;
            }
        }

        if (destination == null) {
            getInterface(ILanguage.class).message(sender, Message.General.NOT_VALID_PLAYER,
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
