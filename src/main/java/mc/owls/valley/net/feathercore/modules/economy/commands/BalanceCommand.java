package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.ChatUtils;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import net.milkbowl.vault.economy.Economy;

public class BalanceCommand implements IFeatherCommand {
    private Economy economy = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.messages = core.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
        this.economy = core.getEconomy();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.general.balance")) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (args.length != 0) { // console and players can see other player's balance
            if (args.length != 1) {
                ChatUtils.sendMessage(commandSender, this.messages, Message.USAGE_INVALID, Message.USAGE_BALANCE);
                return true;
            }

            final String playerName = args[0];
            final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

            if (!player.hasPlayedBefore()) {
                ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_PLAYER,
                        Pair.of(Placeholder.STRING, playerName));
                return true;
            }

            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.BALANCE_OTHER,
                    Pair.of(Placeholder.PLAYER_NAME, playerName),
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance(player))));
        } else if (commandSender instanceof Player) { // players can see their own balance
            if (!commandSender.hasPermission("feathercore.economy.general.balance")) {
                ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
                return true;
            }

            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.BALANCE_SELF,
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) commandSender))));
        } else { // console can't see its own balance (lol)
            ChatUtils.sendMessage(commandSender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
            final String alias, final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final var arg = args[0];
            final List<String> onlinePlayers = StringUtils.getOnlinePlayers();

            if (arg.isEmpty()) {
                completions = onlinePlayers;
            } else {
                completions = StringUtils.filterStartingWith(onlinePlayers, arg);
            }
        }

        return completions;
    }

}
