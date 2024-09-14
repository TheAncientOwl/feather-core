package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;
import net.milkbowl.vault.economy.Economy;

public class PayCommand extends FeatherCommand<PayCommand.CommandData> {
    public static record CommandData(OfflinePlayer receiver, double amount) {
    }

    private Economy economy = null;
    private IPlayersDataManager playersData = null;
    private IPropertyAccessor economyConfig = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.economy = core.getEconomy();
        this.playersData = core.getPlayersDataManager();
        this.economyConfig = core.getConfigurationManager().getEconomyConfigFile();
        this.messages = core.getConfigurationManager().getMessagesConfigFile()
                .getConfigurationSection(Messages.ECONOMY);
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        this.economy.withdrawPlayer((Player) sender, data.amount);
        this.economy.depositPlayer(data.receiver, data.amount);

        final var amount = this.economy.format(data.amount);

        Message.to(sender, this.messages, Messages.PAY_SEND,
                Pair.of(Placeholder.PLAYER_NAME, data.receiver.getName()),
                Pair.of(Placeholder.AMOUNT, amount));
        Message.to((Player) data.receiver, this.messages, Messages.PAY_RECEIVE,
                Pair.of(Placeholder.PLAYER_NAME, ((Player) sender).getName()),
                Pair.of(Placeholder.AMOUNT, amount));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check the basics
        if (!sender.hasPermission("feathercore.economy.general.pay")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 2) {
            Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_PAY);
            return null;
        }

        // 3. check if receiver is player
        final OfflinePlayer receiverPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!receiverPlayer.hasPlayedBefore()) {
            Message.to(sender, this.messages, Messages.NOT_PLAYER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        if (!receiverPlayer.isOnline()) {
            Message.to(sender, this.messages, Messages.NOT_ONLINE_PLAYER,
                    Pair.of(Placeholder.PLAYER_NAME, receiverPlayer.getName()));
            return null;
        }

        // 4. check if receiver accepts payments
        final PlayerModel playerModel = this.playersData.getPlayerModel(receiverPlayer);
        if (playerModel == null) {
            return null;
        }

        if (!playerModel.acceptsPayments && !sender.hasPermission("feathercore.economy.general.pay.override")) {
            Message.to(sender, this.messages, Messages.PAY_TOGGLE_NOT_ACCEPTING,
                    Pair.of(Placeholder.PLAYER_NAME, receiverPlayer.getName()));
            return null;
        }

        // 5. parse amount
        double amount = 0;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (final Exception e) {
            Message.to(sender, this.messages, Messages.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return null;
        }

        // 6. check if amount is viable to be transferred
        final var minAmount = this.economyConfig.getDouble("minimum-pay-amount");
        if (amount < minAmount) {
            Message.to(sender, this.messages, Messages.PAY_MIN_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, minAmount));
            return null;
        }

        if (!this.economy.has((Player) sender, amount)) {
            Message.to(sender, this.messages, Messages.PAY_NO_FUNDS);
            return null;
        }

        final var maxBalance = this.economyConfig.getDouble("money.max");
        if (this.economy.getBalance(receiverPlayer) + amount > maxBalance) {
            Message.to(sender, this.messages, Messages.PAY_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, maxBalance));
            return null;
        }

        return new CommandData(receiverPlayer, amount);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
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
