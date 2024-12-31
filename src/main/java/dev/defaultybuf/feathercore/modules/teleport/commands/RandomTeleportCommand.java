/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file RandomTeleportCommand.java
 * @author Alexandru Delegeanu
 * @version 0.10
 * @description Teleport the player at a random location in the world
 */

package dev.defaultybuf.feathercore.modules.teleport.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.Args;
import dev.defaultybuf.feathercore.api.common.util.Clock;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.common.util.TimeUtils;
import dev.defaultybuf.feathercore.api.core.FeatherCommand;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class RandomTeleportCommand extends FeatherCommand<RandomTeleportCommand.CommandData> {
    public RandomTeleportCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(Player who) {
    }

    private Map<UUID, Long> playersToRtpTime = new HashMap<>();

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));
        final var world = data.who.getWorld();

        if (!sender.hasPermission("feathercore.teleport.random.self." + world.getName()) ||
                (!selfTeleport && !sender
                        .hasPermission("feathercore.teleport.random.other." + world.getName()))) {
            getLanguage().message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var now = Clock.currentTimeMillis();

        final boolean selfTeleport = (sender instanceof Player && data.who.equals((Player) sender));

        if (sender instanceof Player
                && !sender.hasPermission("feathercore.teleport.random.bypass-cooldown")) {
            final var rtpTime = this.playersToRtpTime.get(((Player) sender).getUniqueId());
            final var cooldown =
                    getInterface(ITeleport.class).getConfig().getMillis("random.cooldown");

            if (rtpTime != null && rtpTime + cooldown > now) {
                getLanguage().message(sender, Message.Teleport.RTP_COOLDOWN,
                        Pair.of(Placeholder.COOLDOWN,
                                TimeUtils.formatRemaining(rtpTime, cooldown)));
                return;
            }
        }

        getLanguage().message(sender, Message.Teleport.RTP_TRY);

        final Location location = randomize(data.who.getLocation());

        if (location != null) {
            getInterface(ITeleport.class).teleport(data.who, location);

            if (sender instanceof Player) {
                this.playersToRtpTime.put(((Player) sender).getUniqueId(), now);
            }

            if (selfTeleport) {
                getLanguage().message(sender, Message.Teleport.RTP_SELF, List.of(
                        Pair.of(Placeholder.WORLD, location.getWorld().getName()),
                        Pair.of(Placeholder.X, location.getX()),
                        Pair.of(Placeholder.Y, location.getY()),
                        Pair.of(Placeholder.Z, location.getZ())));
            } else {
                getLanguage().message(sender, Message.Teleport.RTP_OTHER, List.of(
                        Pair.of(Placeholder.PLAYER, data.who.getName()),
                        Pair.of(Placeholder.WORLD, location.getWorld().getName()),
                        Pair.of(Placeholder.X, location.getX()),
                        Pair.of(Placeholder.Y, location.getY()),
                        Pair.of(Placeholder.Z, location.getZ())));
            }
        } else {
            getLanguage().message(sender, Message.Teleport.RTP_FAIL);
        }
    }

    private Location randomize(final Location location) {
        World world = location.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        Random random = new Random();

        final var worldConfig = getInterface(ITeleport.class).getConfig()
                .getConfigurationSection("random." + world.getName());

        final var trials = worldConfig.getInt("trials");
        final var minDistance = worldConfig.getInt("min-distance");
        final var distanceDiff = worldConfig.getInt("max-distance") - minDistance + 1;
        final var minAltitude = worldConfig.getInt("altitude.min");
        final var maxAltitude = worldConfig.getInt("altitude.max");

        for (int i = 0; i < trials; ++i) {
            final var rand = random.nextInt(distanceDiff) + minDistance;

            double x = (int) (location.getX()) + rand + 0.5D;
            double z = (int) (location.getZ()) + rand + 0.5D;
            int y = world.getHighestBlockYAt((int) x, (int) z);

            final var randLocation =
                    new Location(world, x, y, z, location.getYaw(), location.getPitch());

            if (!worldBorder.isInside(randLocation)) {
                continue;
            }

            if (isSafe(randLocation)) {
                return randLocation;
            }

            for (int altitude = maxAltitude; altitude > minAltitude; --altitude) {
                randLocation.setY(altitude);
                if (isSafe(randLocation)) {
                    return randLocation;
                }

                if (randLocation.getBlock().isLiquid()) {
                    break;
                }
            }
        }

        return null;
    }

    public boolean isSafe(final Location location) {
        final var block = location.getBlock();
        final var down = block.getRelative(BlockFace.DOWN);
        final var up = block.getRelative(BlockFace.UP);

        return !block.isLiquid() && !block.isSolid() && down.isSolid() && !up.isLiquid()
                && !up.isSolid();
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        Player who = null;

        switch (args.length) {
            case 0: {
                // /rtp
                if (!(sender instanceof Player)) {
                    getLanguage().message(sender, Message.General.PLAYERS_ONLY);
                    return null;
                }
                who = (Player) sender;
                break;
            }
            case 1: {
                // /rtp [player]
                final var parsedArgs = Args.parse(args, Args::getOnlinePlayer);

                if (parsedArgs.success()) {
                    who = parsedArgs.getPlayer(0);
                } else {
                    getLanguage().message(sender, Message.General.NOT_ONLINE_PLAYER,
                            Pair.of(Placeholder.PLAYER, args[0]));
                    return null;
                }

                break;
            }
            default: {
                getLanguage().message(sender, Message.General.USAGE_INVALID,
                        Message.Teleport.USAGE_RTP);
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
                completions =
                        StringUtils.filterStartingWith(StringUtils.getOnlinePlayers(), args[0]);
                break;
            default:
                break;
        }

        return completions;
    }

}
