/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Args.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Utility for parsing objects from command string args
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Args {
    public static final record ParseResult(Object[] args, int failIndex) {
        public static final int PARSE_SUCCESS_INDEX = -1;

        public boolean success() {
            return this.failIndex == ParseResult.PARSE_SUCCESS_INDEX;
        }

        public String getString(int index) {
            return (String) args[index];
        }

        public double getDouble(int index) {
            return (double) args[index];
        }

        public int getInt(int index) {
            return (int) args[index];
        }

        public Player getPlayer(int index) {
            return (Player) args[index];
        }

        public World getWorld(int index) {
            return (World) args[index];
        }
    }

    @SafeVarargs
    public static ParseResult parse(final String[] args, Function<String, Object>... parsers) {
        final var values = new Object[args.length];

        int index = 0;
        for (final var parser : parsers) {
            values[index] = parser.apply(args[index]);
            if (values[index] == null) {
                return new ParseResult(null, index);
            }
            index++;
        }

        return new ParseResult(values, -1);
    }

    public static Double getDouble(final String value) {
        Double out = null;
        try {
            out = Double.parseDouble(value);
        } catch (final Exception e) {
        }
        return out;
    }

    public static Integer getInt(final String value) {
        Integer out = null;
        try {
            out = Integer.parseInt(value);
        } catch (final Exception e) {
        }
        return out;
    }

    public static String getString(final String str) {
        return str;
    }

    public static Player getOnlinePlayer(final String name) {
        return Bukkit.getPlayerExact(name);
    }

    public static World getWorld(final String name) {
        return Bukkit.getWorld(name);
    }
}
