package mc.owls.valley.net.feathercore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import net.md_5.bungee.api.ChatColor;

public class ChatUtils {
    public static String translateColors(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(final CommandSender commandSender, final String message) {
        commandSender.sendMessage(translateColors(message));
    }

    public static void sendMessage(final Player player, final String message) {
        player.sendMessage(translateColors(message));
    }

    @SuppressWarnings("unchecked")
    public static void sendPlaceholderMessage(final CommandSender commandSender, final IPropertyAccessor properties,
            final String key, Pair<String, Object>... placeholders) {
        commandSender
                .sendMessage(translateColors(StringUtils.replacePlaceholders(properties.getString(key), placeholders)));
    }

    public static void sendMessage(final CommandSender commandSender, String... messages) {
        sendMessage(commandSender, String.join("\n", messages));
    }

    public static void sendMessage(final CommandSender commandSender, final IPropertyAccessor propertyAccessor,
            String... keys) {
        final StringBuilder sb = new StringBuilder();

        for (final var key : keys) {
            sb.append(propertyAccessor.getString(key)).append('\n');
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        sendMessage(commandSender, sb.toString());
    }
}
