/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TimeUtils.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Utility class
 */

package mc.owls.valley.net.feathercore.api.common;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class TimeUtils {
    public static String formatElapsed(long startMillis, long endMillis) {
        long elapsed = endMillis - startMillis;
        return formatDuration(elapsed);
    }

    public static String formatRemaining(long startMillis, long duration) {
        final long now = System.currentTimeMillis();
        final long elapsed = now - startMillis;
        final long remaining = duration - elapsed;
        return formatDuration(remaining);
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([hms]{1,2})");

    public static long parseMillis(final String timeString) throws IllegalArgumentException {
        final var matcher = TIME_PATTERN.matcher(timeString);

        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "h":
                    return value * 3600000;
                case "m":
                    return value * 60000;
                case "s":
                    return value * 1000;
                case "ms":
                    return value;
                default:
                    throw new IllegalArgumentException("Invalid time unit: " + unit);
            }
        } else {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }
    }

    public static long parseSeconds(String timeString) {
        final var matcher = TIME_PATTERN.matcher(timeString);

        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "h":
                    return value * 3600;
                case "m":
                    return value * 60;
                case "s":
                    return value;
                case "ms":
                    return value / 1000;
                default:
                    throw new IllegalArgumentException("Invalid time unit: " + unit);
            }
        } else {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }
    }

    private static String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        long milliseconds = millis;

        final StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(seconds).append("s");
        }
        if (milliseconds > 0) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(milliseconds).append("ms");
        }

        return sb.toString();
    }

}
