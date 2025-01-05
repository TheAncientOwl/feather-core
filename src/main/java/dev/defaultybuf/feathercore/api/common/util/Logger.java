/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Logger.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Debug logger utility
 */
package dev.defaultybuf.feathercore.api.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String RESET = "\u001B[0m";
    private static final String LIGHT_GRAY = "\u001B[37m";
    private static final String DARK_GREEN = "\u001B[32m";
    private static final String GREEN = "\u001B[92m";
    private static final String BLUE = "\u001B[34m";
    private static final String GOLD = "\u001B[33m";
    private static final String YELLOW = "\u001B[93m";
    private static final String DARK_RED = "\u001B[31m";
    private static final String RED = "\u001B[91m";

    private Logger() {}

    public static void debug(Object object) {
        log("DEBUG", object, DARK_GREEN, GREEN);
    }

    public static void info(Object object) {
        log("INFO", object, BLUE, LIGHT_GRAY);
    }

    public static void warn(Object object) {
        log("WARN", object, GOLD, YELLOW);
    }

    public static void error(Object object) {
        log("ERROR", object, DARK_RED, RED);
    }

    private static void log(String level, Object object, String levelColor, String messageColor) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(String.format("%s[%s] [%s%s%s] %s%s%s",
                LIGHT_GRAY, timestamp, levelColor, level, LIGHT_GRAY, messageColor, object, RESET));
    }
}
