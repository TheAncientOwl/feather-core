package mc.owls.valley.net.feathercore.modules.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import net.milkbowl.vault.economy.Economy;

public class EcoCommand implements IFeatherCommand {
    private Economy economy = null;
    private IConfigSection config = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.config = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
        this.economy = plugin.getEconomy();
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        return true;
    }
}
