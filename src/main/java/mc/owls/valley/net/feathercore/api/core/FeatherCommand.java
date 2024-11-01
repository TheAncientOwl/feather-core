/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Base class for plugin command
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class FeatherCommand<CommandData> implements CommandExecutor, TabCompleter {
    @Override
    public final boolean onCommand(final CommandSender sender, final Command cmd, final String label,
            final String[] args) {
        final CommandData data = parse(sender, args);

        if (data != null && hasPermission(sender, data)) {
            execute(sender, data);
        }

        return true;
    }

    @Override
    public final List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
        return onTabComplete(args);
    }

    protected abstract CommandData parse(final CommandSender sender, final String[] args);

    protected abstract boolean hasPermission(final CommandSender sender, final CommandData data);

    protected abstract void execute(final CommandSender sender, final CommandData data);

    protected abstract List<String> onTabComplete(final String[] args);

    public abstract void onCreate(final IFeatherCoreProvider core);
}
