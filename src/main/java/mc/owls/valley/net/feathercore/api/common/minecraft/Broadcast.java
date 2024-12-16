/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Broadcast.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Utility functions for broadcasting messages to players
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import java.util.List;

import org.bukkit.Bukkit;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;

public class Broadcast {

    public static void broadcast(final String message) {
        final String coloredMessage = message;
        for (final var player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(coloredMessage);
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
