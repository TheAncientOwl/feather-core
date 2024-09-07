package mc.owls.valley.net.feathercore.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {
    public static String exceptionToStr(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    @SuppressWarnings("unchecked")
    public static String replacePlaceholders(String message, Pair<String, Object>... replacements) {
        for (final var replacement : replacements) {
            message = message.replace(replacement.first, replacement.second.toString());
        }
        return message;
    }
}
