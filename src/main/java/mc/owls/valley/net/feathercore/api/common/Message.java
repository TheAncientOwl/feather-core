package mc.owls.valley.net.feathercore.api.common;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;

public class Message {

    public static void to(final CommandSender reciver, final String message) {
        reciver.sendMessage(StringUtils.translateColors(message));
    }

    public static void to(final CommandSender reciver, String... messages) {
        reciver.sendMessage(StringUtils.translateColors(String.join("\n", messages)));
    }

    public static void to(final CommandSender reciver, final IPropertyAccessor propertyAccessor,
            final String key) {
        reciver.sendMessage(StringUtils.translateColors(propertyAccessor.getString(key)));
    }

    public static void to(final CommandSender reciver, final IPropertyAccessor propertyAccessor,
            String... keys) {
        final StringBuilder sb = new StringBuilder();

        for (final var key : keys) {
            sb.append(propertyAccessor.getString(key)).append('\n');
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        reciver.sendMessage(StringUtils.translateColors(sb.toString()));
    }

    @SuppressWarnings("unchecked")
    public static void to(final CommandSender reciver, final IPropertyAccessor properties,
            final String key, Pair<String, Object>... placeholders) {
        reciver
                .sendMessage(StringUtils
                        .translateColors(StringUtils.replacePlaceholders(properties.getString(key), placeholders)));
    }

}
