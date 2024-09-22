package mc.owls.valley.net.feathercore.api.common;

import java.util.concurrent.TimeUnit;

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
