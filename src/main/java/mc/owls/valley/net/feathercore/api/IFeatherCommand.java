package mc.owls.valley.net.feathercore.api;

import org.bukkit.command.CommandExecutor;

import mc.owls.valley.net.feathercore.core.FeatherCore;

public interface IFeatherCommand extends CommandExecutor {
    public void onCreate(final FeatherCore plugin);
}
