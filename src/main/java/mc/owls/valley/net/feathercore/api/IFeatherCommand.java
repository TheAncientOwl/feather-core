package mc.owls.valley.net.feathercore.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import mc.owls.valley.net.feathercore.core.FeatherCore;

public interface IFeatherCommand extends CommandExecutor, TabCompleter {
    public void onCreate(final FeatherCore plugin);
}
