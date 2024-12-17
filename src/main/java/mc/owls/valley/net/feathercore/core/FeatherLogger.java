/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherLogger.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Plugin logger
 */

package mc.owls.valley.net.feathercore.core;

import org.bukkit.command.ConsoleCommandSender;

import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;

public class FeatherLogger implements IFeatherLogger {
    public static final String PLUGIN_TAG = "&8[&eFeather&6Core&8]&r ";

    private ConsoleCommandSender console = null;

    public FeatherLogger(final ConsoleCommandSender console) {
        this.console = console;
    }

    public void info(final String message) {
        sendMessage("&8» &3" + message);
    }

    public void warn(final String message) {
        sendMessage("&8» &e" + message);
    }

    public void error(final String message) {
        sendMessage("&8» &c" + message);
    }

    public void debug(final String message) {
        sendMessage("&8» &a" + message);
    }

    private void sendMessage(final String message) {
        this.console.sendMessage(StringUtils.translateColors(PLUGIN_TAG + message));
    }
}
