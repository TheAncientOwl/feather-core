package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.common.Placeholder;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.utils.ChatUtils;
import mc.owls.valley.net.feathercore.utils.Pair;
import mc.owls.valley.net.feathercore.utils.StringUtils;
import net.milkbowl.vault.economy.Economy;

public class PayCommand implements IFeatherCommand {
    private Economy economy = null;
    private IPropertyAccessor economyConfig = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.economyConfig = plugin.getConfigurationManager().getEconomyConfigFile();
        this.messages = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
        this.economy = plugin.getEconomy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.general.pay")) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 2) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.USAGE_INVALID, Message.USAGE_PAY);
            return true;
        }

        final OfflinePlayer receiverPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!receiverPlayer.hasPlayedBefore()) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_PLAYER,
                    Pair.of(Placeholder.STRING, args[0]));
            return true;
        }

        if (!receiverPlayer.isOnline()) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_ONLINE_PLAYER,
                    Pair.of(Placeholder.PLAYER_NAME, receiverPlayer.getName()));
            return true;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (final Exception e) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return true;
        }

        final var minAmount = this.economyConfig.getDouble("minimum-pay-amount");
        if (amount < minAmount) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.PAY_MIN_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, minAmount));
            return true;
        }

        if (!this.economy.has((Player) commandSender, amount)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PAY_NO_FUNDS);
            return true;
        }

        final var maxBalance = this.economyConfig.getDouble("money.max");
        if (this.economy.getBalance(receiverPlayer) + amount > maxBalance) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.PAY_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, maxBalance));
            return true;
        }

        this.economy.withdrawPlayer((Player) commandSender, amount);
        this.economy.depositPlayer(receiverPlayer, amount);

        ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.PAY_SEND,
                Pair.of(Placeholder.PLAYER_NAME, receiverPlayer.getName()), Pair.of(Placeholder.AMOUNT, amount));
        ChatUtils.sendPlaceholderMessage((Player) receiverPlayer, this.messages, Message.PAY_RECEIVE,
                Pair.of(Placeholder.PLAYER_NAME, ((Player) commandSender).getName()),
                Pair.of(Placeholder.AMOUNT, amount));

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
        } else if (args.length == 2) {
            completions.add("amount");
        }

        return completions;
    }

}
