/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file WorldUtils.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utilities for world stuff
 */
package dev.defaultybuf.feathercore.common.minecraft;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;

public class WorldUtils {
    public static boolean isSafe(final Location location) {
        final var block = location.getBlock();
        final var down = block.getRelative(BlockFace.DOWN);
        final var up = block.getRelative(BlockFace.UP);

        return !block.isLiquid() && !block.isSolid() && down.isSolid() && !up.isLiquid()
                && !up.isSolid();
    }

    public static record RandomLocationRestrictions(
            int trials,
            int minDistance, int maxDistance,
            int minAltitude, int maxAltitude) {
    }

    public static Location randomize(final Location location,
            final RandomLocationRestrictions restrictions) {
        World world = location.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        Random random = new Random();

        final var distanceDiff = restrictions.maxDistance - restrictions.minDistance + 1;

        for (int trial = 0; trial < restrictions.trials; ++trial) {
            final var rand = random.nextInt(distanceDiff) + restrictions.minDistance;

            double x = (int) (location.getX()) + rand + 0.5D;
            double z = (int) (location.getZ()) + rand + 0.5D;
            int y = world.getHighestBlockYAt((int) x, (int) z);

            final var randLocation =
                    new Location(world, x, y, z, location.getYaw(), location.getPitch());

            if (!worldBorder.isInside(randLocation)) {
                continue;
            }

            if (WorldUtils.isSafe(randLocation)) {
                return randLocation;
            }

            for (int altitude =
                    restrictions.maxAltitude; altitude > restrictions.minAltitude; --altitude) {
                randLocation.setY(altitude);
                if (WorldUtils.isSafe(randLocation)) {
                    return randLocation;
                }

                if (randLocation.getBlock().isLiquid()) {
                    break;
                }
            }
        }

        return null;
    }
}
