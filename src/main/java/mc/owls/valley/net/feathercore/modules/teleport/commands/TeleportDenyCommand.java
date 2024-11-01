/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportDenyCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Deny a teleport request
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
import mc.owls.valley.net.feathercore.modules.teleport.common.Message;
import mc.owls.valley.net.feathercore.modules.teleport.components.Teleport;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class TeleportDenyCommand extends FeatherCommand<TeleportDenyCommand.CommandData> {
    public static record CommandData(Player issuer, Player target) {
    }

    private Teleport teleport = null;
    private TranslationManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.teleport = core.getTeleport();
        this.lang = core.getTranslationManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.teleport.request.deny")) {
            this.lang.message(sender, Message.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (this.teleport.cancelRequest(data.issuer, data.target)) {
            case NO_SUCH_REQUEST: {
                this.lang.message(sender, Message.TELEPORT_NO_SUCH_REQUEST);
                break;
            }
            case CANCELLED: {
                this.lang.message(data.issuer, Message.TELEPORT_REQUEST_DECLINE_ISSUER,
                        Pair.of(Placeholder.PLAYER, data.target.getName()));
                this.lang.message(data.target, Message.TELEPORT_REQUEST_DECLINE_TARGET,
                        Pair.of(Placeholder.PLAYER, data.issuer.getName()));
                break;
            }
            default: {
                throw new Error(
                        "Logic error: TeleportDenyCommand.java::execute(CommandSender, CommandData). Please notify developer");
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player issuer = null;
        Player target = null;

        switch (args.length) {
            case 1: {
                // /tpdeny issuer-player
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    if (!(sender instanceof Player)) {
                        this.lang.message(sender, Message.PLAYERS_ONLY);
                        return null;
                    }

                    issuer = parsedArgs.getPlayer(0);
                    target = (Player) sender;
                } else {
                    this.lang.message(sender, Message.PLAYER_NOT_ONLINE, Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_TELEPORT_REQUEST_DENY);
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
