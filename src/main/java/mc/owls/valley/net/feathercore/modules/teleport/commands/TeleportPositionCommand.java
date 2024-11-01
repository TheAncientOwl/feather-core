/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportPositionCommand.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Teleport to specified position in the world
 */

package mc.owls.valley.net.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
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

public class TeleportPositionCommand extends FeatherCommand<TeleportPositionCommand.CommandData> {
    public static record CommandData(Player player, double x, double y, double z, World world) {
    }

    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.lang = core.getLanguageManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.player.equals((Player) sender));

        if (!sender.hasPermission("feathercore.teleport." + data.world.getName() + ".position") ||
                (!selfTeleport
                        && !sender.hasPermission("feathercore.teleport." + data.world.getName() + ".position.other"))) {
            this.lang.message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.player.equals((Player) sender));

        Teleport.teleport(data.player, data.x, data.y, data.z, data.world);

        this.lang.message(data.player, Message.Teleport.POSITION,
                Pair.of(Placeholder.X, (int) data.x),
                Pair.of(Placeholder.Y, (int) data.y),
                Pair.of(Placeholder.Z, (int) data.z),
                Pair.of(Placeholder.WORLD, data.world.getName()));

        if (!selfTeleport) {
            this.lang.message(sender, Message.Teleport.POSITION_OTHER,
                    Pair.of(Placeholder.PLAYER, data.player.getName()),
                    Pair.of(Placeholder.X, (int) data.x),
                    Pair.of(Placeholder.Y, (int) data.y),
                    Pair.of(Placeholder.Z, (int) data.z),
                    Pair.of(Placeholder.WORLD, data.world.getName()));
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player player = null;
        double x = 0;
        double y = 0;
        double z = 0;
        World world = null;

        switch (args.length) {
            case 3: {
                // /tppos [x] [y] [z]
                final var parsedArgs = Args.parse(args, Args::getDouble, Args::getDouble, Args::getDouble);

                if (parsedArgs.success()) {
                    x = parsedArgs.getDouble(0);
                    y = parsedArgs.getDouble(1);
                    z = parsedArgs.getDouble(2);
                } else {
                    this.lang.message(sender, Message.General.NAN,
                            Pair.of(Placeholder.STRING, args[parsedArgs.failIndex()]));
                    return null;
                }
                break;
            }
            case 4: {
                // /tppos [x] [y] [z] (player|world)
                final var parsedArgs = Args.parse(args, Args::getDouble, Args::getDouble, Args::getDouble,
                        Args::getString);

                if (parsedArgs.success()) {
                    x = parsedArgs.getDouble(0);
                    y = parsedArgs.getDouble(1);
                    z = parsedArgs.getDouble(2);

                    // player or world
                    final Player argPlayer = Bukkit.getPlayerExact(parsedArgs.getString(3));
                    if (argPlayer != null) {
                        player = argPlayer;
                    } else {
                        final World argWorld = Bukkit.getWorld(parsedArgs.getString(3));
                        if (argWorld != null) {
                            world = argWorld;
                        } else {
                            this.lang.message(sender, Message.General.NOT_VALID_VALUE,
                                    Pair.of(Placeholder.STRING, args[3]));
                            return null;
                        }
                    }
                } else if (parsedArgs.failIndex() >= 0 && parsedArgs.failIndex() <= 2) {
                    this.lang.message(sender, Message.General.NAN,
                            Pair.of(Placeholder.STRING, args[parsedArgs.failIndex()]));
                    return null;
                }
                break;
            }
            case 5: {
                // /tppos [x] [y] [z] (world) (player)
                final var parsedArgs = Args.parse(args, Args::getDouble, Args::getDouble, Args::getDouble,
                        Args::getWorld, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    x = parsedArgs.getDouble(0);
                    y = parsedArgs.getDouble(1);
                    z = parsedArgs.getDouble(2);
                    world = parsedArgs.getWorld(3);
                    player = parsedArgs.getPlayer(4);
                } else
                    switch (parsedArgs.failIndex()) {
                        case 0:
                        case 1:
                        case 2:
                            this.lang.message(sender, Message.General.NAN,
                                    Pair.of(Placeholder.STRING, args[parsedArgs.failIndex()]));
                            return null;
                        case 3:
                            this.lang.message(sender, Message.General.NOT_VALID_WORLD,
                                    Pair.of(Placeholder.STRING, args[parsedArgs.failIndex()]));
                            return null;
                        case 4:
                            this.lang.message(sender, Message.General.NOT_VALID_PLAYER,
                                    Pair.of(Placeholder.STRING, args[parsedArgs.failIndex()]));
                            return null;
                    }
                break;
            }
            default: {
                this.lang.message(sender, Message.General.USAGE_INVALID, Message.Teleport.USAGE_POSITION);
                return null;
            }
        }

        if (player == null) {
            if (!(sender instanceof Player)) {
                this.lang.message(sender, Message.General.PLAYERS_ONLY);
                return null;
            }
            player = (Player) sender;
        }

        if (world == null) {
            world = player.getWorld();
        }

        return new CommandData(player, x, y, z, world);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                completions.add("x");
                break;
            case 2:
                completions.add("y");
                break;
            case 3:
                completions.add("z");
                break;
            case 4:
                completions = StringUtils.filterStartingWith(StringUtils.getWorlds(), args[3]);
                break;
            case 5:
                completions = StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[4]);
                break;
        }

        return completions;
    }

}
