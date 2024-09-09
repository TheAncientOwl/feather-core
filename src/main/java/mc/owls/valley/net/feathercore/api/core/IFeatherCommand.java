package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface IFeatherCommand extends CommandExecutor, TabCompleter {
    public void onCreate(final IFeatherCoreProvider core);
}
