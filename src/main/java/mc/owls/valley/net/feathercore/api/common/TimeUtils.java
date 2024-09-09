package mc.owls.valley.net.feathercore.api.common;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String formatDuration(long startMillis, long endMillis) {
        long millis = endMillis - startMillis;

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
                sb.append("&8,&b ");
            }
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            if (!sb.isEmpty()) {
                sb.append("&8,&b ");
            }
            sb.append(seconds).append("s");
        }
        if (milliseconds > 0) {
            if (!sb.isEmpty()) {
                sb.append("&8,&b ");
            }
            sb.append(milliseconds).append("ms");
        }

        return sb.toString();
    }
}
