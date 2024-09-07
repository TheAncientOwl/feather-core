package mc.owls.valley.net.feathercore.modules.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class BalanceCommand implements IFeatherCommand {

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
