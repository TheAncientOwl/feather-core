package mc.owls.valley.net.feathercore.api.common;

import org.bukkit.Bukkit;

import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IPropertyAccessor;

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

    @SafeVarargs
    public static void broadcast(final IPropertyAccessor properties,
            final String key, Pair<String, Object>... placeholders) {
        broadcast(StringUtils.translateColors(
                StringUtils.replacePlaceholders(properties.getString(key), placeholders)));
    }

}
