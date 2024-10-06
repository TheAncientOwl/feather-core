/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ArgsParser.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility for parsing objects from command string args
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import java.util.function.Function;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArgsParser {
    public static final int PARSE_SUCCESS_INDEX = -1;

    public static final record ParseResult(Object[] args, int index) {
        public String getString(int index) {
            return (String) args[index];
        }

        public double getDouble(int index) {
            return (double) args[index];
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

        return new ParseResult(values, ArgsParser.PARSE_SUCCESS_INDEX);
    }

    public static Double parseDouble(final String value) {
        Double out = null;
        try {
            out = Double.parseDouble(value);
        } catch (final Exception e) {
        }
        return out;
    }

    public static String parseString(final String str) {
        return str;
    }
}
