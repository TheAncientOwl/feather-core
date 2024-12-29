/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file StringUtils.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Utility class
 */

package dev.defaultybuf.feathercore.api.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import net.md_5.bungee.api.ChatColor;

public class StringUtils {

    public static String exceptionToStr(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String replacePlaceholders(String message,
            final List<Pair<String, Object>> replacements) {
        for (final var replacement : replacements) {
            message = message.replace(replacement.first, replacement.second.toString());
        }
        return message;
    }

    public static String replacePlaceholders(String message,
            final Pair<String, Object> replacement) {
        return message.replace(replacement.first, replacement.second.toString());
    }

    public static List<String> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    public static List<String> getWorlds() {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .toList();
    }

    public static List<String> filterStartingWith(final List<String> list, String what) {
        what = what.toLowerCase();

        List<String> out = new ArrayList<>();

        for (final var str : list) {
            if (str.toLowerCase().startsWith(what)) {
                out.add(str);
            }
        }

        return out;
    }

    public static String translateColors(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
