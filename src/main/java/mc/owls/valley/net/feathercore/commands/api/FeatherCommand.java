package mc.owls.valley.net.feathercore.commands.api;

import org.bukkit.command.CommandExecutor;

import mc.owls.valley.net.feathercore.core.FeatherCore;

public interface FeatherCommand extends CommandExecutor {
    public void onCreate(final FeatherCore plugin);
}
