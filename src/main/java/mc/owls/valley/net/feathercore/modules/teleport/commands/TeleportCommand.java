/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Teleport to a player, or teleport player1 to player2
 */

package mc.owls.valley.net.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.Args;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.teleport.common.Message;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;

public class TeleportCommand extends FeatherCommand<TeleportCommand.CommandData> {
    public static record CommandData(Player who, Player destination) {
    }

    private Teleport teleport = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.teleport = core.getTeleport();
        this.lang = core.getLanguageManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        if (!sender.hasPermission("feathercore.teleport.player.self")
                || (!selfTeleport && !sender.hasPermission("feathercore.teleport.player.other"))) {
            this.lang.message(sender, Message.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        this.teleport.teleport(data.who, data.destination);

        if (selfTeleport) {
            this.lang.message(sender, Message.TELEPORT_PLAYER_SELF,
                    Pair.of(Placeholder.PLAYER, data.destination.getName()));
        } else {
            this.lang.message(sender, Message.TELEPORT_PLAYER,
                    Pair.of(Placeholder.PLAYER1, data.who.getName()),
                    Pair.of(Placeholder.PLAYER2, data.destination.getName()));
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player who = null;
        Player destination = null;

        switch (args.length) {
            case 1: {
                // /tp [destination-player]
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        this.lang.message(sender, Message.PLAYERS_ONLY);
                        return null;
                    }

                    who = (Player) sender;
                    destination = parsedArgs.getPlayer(0);
                } else {
                    this.lang.message(sender, Message.PLAYER_NOT_ONLINE, Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            case 2: {
                // /tp [who-player] [destination-player]
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    who = parsedArgs.getPlayer(0);
                    destination = parsedArgs.getPlayer(1);
                } else {
                    this.lang.message(sender, Message.PLAYER_NOT_ONLINE,
                            Pair.of(Placeholder.PLAYER, args[parsedArgs.failIndex()]));
                    return null;
                }

                break;
            }
            default: {
                this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_PLAYER);
                return null;
            }
        }

        return new CommandData(who, destination);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            case 2:
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[1]);
                break;
            default:
                break;
        }

        return completions;
    }

}
