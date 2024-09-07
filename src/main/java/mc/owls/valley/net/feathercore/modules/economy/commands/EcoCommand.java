package mc.owls.valley.net.feathercore.modules.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.core.common.Placeholder;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.utils.ChatUtils;
import mc.owls.valley.net.feathercore.utils.Pair;
import net.milkbowl.vault.economy.Economy;

public class EcoCommand implements IFeatherCommand {
    private Economy economy = null;
    private IPropertyAccessor config = null;
    private IPropertyAccessor economyConfig = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.config = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
        this.economyConfig = plugin.getConfigurationManager().getEconomyConfigFile();
        this.economy = plugin.getEconomy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.setup.eco")) {
            ChatUtils.sendMessage(commandSender, this.config.getString(Message.PERMISSION_DENIED));
            return true;
        }

        if (args.length == 3) {
            final String actionStr = args[0].toLowerCase();
            final String playerName = args[1];
            final String amountStr = args[2];

            final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if (!player.hasPlayedBefore()) {
                ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.NOT_PLAYER,
                        Pair.of(Placeholder.STRING, playerName));
                return true;
            }

            double amount = 0;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (final Exception e) {
                ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.NOT_VALID_NUMBER,
                        Pair.of(Placeholder.STRING, amountStr));
                return true;
            }

            if (amount < 0 && (actionStr.equals("give") || actionStr.equals("take"))) {
                ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_NO_NEGATIVE_AMOUNT,
                        Pair.of(Placeholder.STRING, actionStr));
                return true;
            }

            final double oldBalance = this.economy.getBalance(player);

            switch (actionStr) {
                case "give": {
                    final var max = this.economyConfig.getDouble("money.max");
                    if (oldBalance + amount > max) {
                        ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_BOUNDS_MAX,
                                Pair.of(Placeholder.MAX, this.economy.format(max)));
                        return true;
                    }
                    this.economy.depositPlayer(player, amount);
                    break;
                }
                case "take": {
                    final var min = this.economyConfig.getDouble("money.min");
                    if (oldBalance - amount < min) {
                        ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_BOUNDS_MIN,
                                Pair.of(Placeholder.MIN, this.economy.format(min)));
                        return true;
                    }
                    this.economy.withdrawPlayer(player, amount);
                    break;
                }
                case "set": {
                    final var max = this.economyConfig.getDouble("money.max");
                    if (amount > max) {
                        ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_BOUNDS_MAX,
                                Pair.of(Placeholder.MAX, this.economy.format(max)));
                        return true;
                    }

                    final var min = this.economyConfig.getDouble("money.min");
                    if (amount < min) {
                        ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_BOUNDS_MIN,
                                Pair.of(Placeholder.MIN, this.economy.format(min)));
                        return true;
                    }

                    this.economy.withdrawPlayer(player, oldBalance);
                    this.economy.depositPlayer(player, amount);
                    break;
                }
                default: {
                    ChatUtils.sendMessages(commandSender, this.config, Message.USAGE_INVALID, Message.USAGE_ECO);
                    return true;
                }
            }

            final double newBalance = this.economy.getBalance(player);
            ChatUtils.sendPlaceholderMessage(commandSender, this.config, Message.ECO_SUCCESS,
                    Pair.of(Placeholder.PLAYER_NAME, player.getName()),
                    Pair.of(Placeholder.OLD, this.economy.format(oldBalance)),
                    Pair.of(Placeholder.NEW, this.economy.format(newBalance)));
            return true;
        }

        ChatUtils.sendMessages(commandSender, this.config, Message.USAGE_INVALID,
                Message.USAGE_ECO);
        return true;
    }
}
