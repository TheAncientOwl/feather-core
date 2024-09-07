package mc.owls.valley.net.feathercore.modules.economy.manager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.commands.api.FeatherCommand;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class BalanceCommand implements FeatherCommand {

    @Override
    public void onCreate(final FeatherCore plugin) {
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        commandSender.sendMessage("Command not implemented yet!");
        return false;
    }

}
