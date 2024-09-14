package mc.owls.valley.net.feathercore.api.common;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;

public class Message {

    public static void to(final CommandSender receiver, final String message) {
        receiver.sendMessage(StringUtils.translateColors(message));
    }

    public static void to(final CommandSender receiver, String... messages) {
        receiver.sendMessage(StringUtils.translateColors(String.join("\n", messages)));
    }

    public static void to(final CommandSender receiver, final IPropertyAccessor propertyAccessor,
            final String key) {
        receiver.sendMessage(StringUtils.translateColors(propertyAccessor.getString(key)));
    }

    public static void to(final CommandSender receiver, final IPropertyAccessor propertyAccessor,
            String... keys) {
        final StringBuilder sb = new StringBuilder();

        for (final var key : keys) {
            sb.append(propertyAccessor.getString(key)).append('\n');
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        receiver.sendMessage(StringUtils.translateColors(sb.toString()));
    }

    @SafeVarargs
    public static void to(final CommandSender receiver, final IPropertyAccessor properties,
            final String key, Pair<String, Object>... placeholders) {
        receiver
                .sendMessage(StringUtils
                        .translateColors(StringUtils.replacePlaceholders(properties.getString(key), placeholders)));
    }

}
