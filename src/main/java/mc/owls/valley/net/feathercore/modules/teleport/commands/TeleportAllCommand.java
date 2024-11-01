/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportAllCommand.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Teleport all players to the command sender
 */

package mc.owls.valley.net.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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

public class TeleportAllCommand extends FeatherCommand<TeleportAllCommand.CommandData> {
    public static record CommandData(Player where) {
    }

    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.lang = core.getLanguageManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.all")) {
            this.lang.message(sender, Message.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        for (final var player : Bukkit.getOnlinePlayers()) {
            Teleport.teleport(player, data.where);
        }

        if (sender instanceof Player && data.where.equals((Player) sender)) {
            this.lang.message(sender, Message.TELEPORT_ALL_SELF);
        } else {
            this.lang.message(sender, Message.TELEPORT_ALL_OTHER, Pair.of(Placeholder.TARGET, data.where.getName()));
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player where = null;

        switch (args.length) {
            case 0: {
                // /tpall
                if (!(sender instanceof Player)) {
                    this.lang.message(sender, Message.PLAYERS_ONLY);
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
                    this.lang.message(sender, Message.PLAYER_NOT_ONLINE, Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_TPALL);
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
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            default:
                break;
        }

        return completions;
    }

}
