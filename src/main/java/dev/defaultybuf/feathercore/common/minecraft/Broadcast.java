/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Broadcast.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Utility functions for broadcasting messages to players
 */

package dev.defaultybuf.feathercore.common.minecraft;

import java.util.List;

import org.bukkit.Bukkit;

import dev.defaultybuf.feather.toolkit.api.configuration.IPropertyAccessor;
import dev.defaultybuf.feather.toolkit.util.java.Pair;
import dev.defaultybuf.feather.toolkit.util.java.StringUtils;

public class Broadcast {

    public static void broadcast(final String message) {
        for (final var player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void broadcast(String... messages) {
        broadcast(StringUtils.translateColors(String.join("\n", messages)));
    }

    public static void broadcast(final IPropertyAccessor propertyAccessor,
            final String key) {
        broadcast(StringUtils.translateColors(propertyAccessor.getString(key)));
    }

    public static void broadcast(final IPropertyAccessor propertyAccessor,
            String... keys) {
        final StringBuilder sb = new StringBuilder();

        for (final var key : keys) {
            sb.append(propertyAccessor.getString(key)).append('\n');
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        broadcast(StringUtils.translateColors(sb.toString()));
    }

    public static void broadcast(final IPropertyAccessor properties,
            final String key, List<Pair<String, Object>> placeholders) {
        broadcast(StringUtils.translateColors(
                StringUtils.replacePlaceholders(properties.getString(key), placeholders)));
    }

}
