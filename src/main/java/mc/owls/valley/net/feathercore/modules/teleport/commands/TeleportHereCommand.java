/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportHereCommand.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Teleport the target player to command sender player
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
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;

public class TeleportHereCommand extends FeatherCommand<TeleportHereCommand.CommandData> {
    public static record CommandData(Player who) {
    }

    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.lang = core.getLanguageManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.here")) {
            this.lang.message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        if (!(sender instanceof Player)) {
            this.lang.message(sender, Message.General.PLAYERS_ONLY);
            return;
        }

        Teleport.teleport(data.who, (Player) sender);

        this.lang.message(sender, Message.Teleport.HERE, Pair.of(Placeholder.PLAYER, data.who.getName()));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player who = null;

        switch (args.length) {
            case 1: {
                // /tphere [player]
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    who = parsedArgs.getPlayer(0);
                } else {
                    this.lang.message(sender, Message.General.NOT_ONLINE_PLAYER, Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                this.lang.message(sender, Message.General.USAGE_INVALID, Message.Teleport.USAGE_TPHERE);
                return null;
            }
        }

        return new CommandData(who);
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
