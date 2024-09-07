package mc.owls.valley.net.feathercore.modules.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.common.Placeholder;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.utils.ChatUtils;
import mc.owls.valley.net.feathercore.utils.Pair;
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
    @SuppressWarnings({ "unchecked" })
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.general.balance")) {
            ChatUtils.sendMessage(commandSender, this.config.getString(Message.PERMISSION_DENIED));
            return true;
        }

        if (args.length != 0) { // console and players can see other player's balance
            if (args.length != 1) {
                ChatUtils.sendMessages(commandSender, this.config, Message.USAGE_INVALID, Message.USAGE_BALANCE);
                return true;
            }

            final String playerName = args[0];
            final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

            if (!player.hasPlayedBefore()) {
                ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.NOT_PLAYER,
                        Pair.of(Placeholder.STRING, playerName));
                return true;
            }

            ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.BALANCE_OTHER,
                    Pair.of(Placeholder.PLAYER_NAME, playerName),
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance(player))));
        } else if (commandSender instanceof Player) { // players can see their own balance
            if (!commandSender.hasPermission("feathercore.economy.general.balance")) {
                ChatUtils.sendMessage(commandSender, this.config.getString(Message.PERMISSION_DENIED));
                return true;
            }

            ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.BALANCE_SELF,
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) commandSender))));
        } else { // console can't see its own balance (lol)
            ChatUtils.sendMessage(commandSender, this.config.getString(Message.COMMAND_SENDER_NOT_PLAYER));
            return true;
        }

        return true;
    }
}
