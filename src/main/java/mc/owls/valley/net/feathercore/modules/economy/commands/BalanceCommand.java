package mc.owls.valley.net.feathercore.modules.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.common.Placeholder;
import mc.owls.valley.net.feathercore.modules.economy.common.MesssageKey;
import mc.owls.valley.net.feathercore.utils.ChatUtils;
import mc.owls.valley.net.feathercore.utils.Pair;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import net.milkbowl.vault.economy.Economy;

public class BalanceCommand implements IFeatherCommand {
    private Economy economy = null;
    private IConfigSection config = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.config = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
        this.economy = plugin.getEconomy();
    }

    @Override
    @SuppressWarnings({ "deprecation", "unchecked" })
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        String message = "";
        String playerName = null;
        double balance = -1;

        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            if (player.hasPermission("feathercore.economy.general.balance")) {
                playerName = player.getName();
                balance = this.economy.getBalance(player);
                message = this.config.getString(MesssageKey.BALANCE_SELF);
            } else {
                message = this.config.getString(MesssageKey.PERMISSION_DENIED);
            }
        } else if (args.length == 1) {
            playerName = args[0];
            balance = this.economy.getBalance(playerName);
            message = this.config.getString(MesssageKey.BALANCE_OTHER);
        } else {
            message = this.config.getString(MesssageKey.COMMAND_SENDER_NOT_PLAYER);
        }

        if (playerName != null) {
            message = StringUtils.replacePlaceholders(message,
                    Pair.of(Placeholder.CURRENCY,
                            balance == 1 || balance == -1 ? this.economy.currencyNameSingular()
                                    : this.economy.currencyNamePlural()),
                    Pair.of(Placeholder.BALANCE, balance),
                    Pair.of(Placeholder.PLAYER_NAME, playerName));
        }

        ChatUtils.sendMessage(commandSender, message);

        return true;
    }
}
