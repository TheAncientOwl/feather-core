/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportHereRequestCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Request teleport the target player to command sender player
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
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport.RequestType;

public class TeleportHereRequestCommand extends FeatherCommand<TeleportHereRequestCommand.CommandData> {
    public static record CommandData(Player issuer, Player target) {
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
        if (!sender.hasPermission("feathercore.teleport.request.here")) {
            this.lang.message(sender, Message.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (this.teleport.request(data.issuer, data.target, RequestType.HERE)) {
            case ALREADY_REQUESTED: {
                this.lang.message(data.issuer, Message.TELEPORT_REQUEST_HERE_EXECUTE_PENDING,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                break;
            }
            case REQUESTED: {
                this.lang.message(data.issuer, Message.TELEPORT_REQUEST_HERE_EXECUTE_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                this.lang.message(data.target, Message.TELEPORT_REQUEST_HERE_EXECUTE_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));
                break;
            }
            default: {
                throw new Error(
                        "Logic error: TeleportHereRequestCommand.java::execute(CommandSender, CommandData). Please notify developer");
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player issuer = null;
        Player target = null;

        switch (args.length) {
            case 1: {
                // /tpahere target-player
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        this.lang.message(sender, Message.PLAYERS_ONLY);
                        return null;
                    }

                    issuer = (Player) sender;
                    target = parsedArgs.getPlayer(0);
                } else {
                    this.lang.message(sender, Message.PLAYER_NOT_ONLINE, Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_TELEPORT_REQUEST_HERE);
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
