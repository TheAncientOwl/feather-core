package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.ChatUtils;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import net.milkbowl.vault.economy.Economy;

public class DepositCommand implements IFeatherCommand {
    private Economy economy = null;
    private IPropertyAccessor economyConfig = null;
    private IPropertyAccessor messages = null;
    private JavaPlugin plugin = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.plugin = core.getPlugin();
        this.economy = core.getEconomy();
        this.economyConfig = core.getConfigurationManager().getEconomyConfigFile();
        this.messages = core.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.general.deposit")) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 1) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.USAGE_INVALID, Message.USAGE_DEPOSIT);
            return true;
        }

        final ItemStack itemInHand = ((Player) commandSender).getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null
                || itemInHand.getItemMeta().getPersistentDataContainer() == null) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.BANKNOTE_INVALID);
            return true;
        }

        final NamespacedKey namespacedKey = new NamespacedKey(this.plugin, Message.BANKNOTE_METADATA_KEY);
        final PersistentDataContainer dataContainer = itemInHand.getItemMeta().getPersistentDataContainer();
        if (!dataContainer.has(new NamespacedKey(this.plugin, Message.BANKNOTE_METADATA_KEY))) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.BANKNOTE_INVALID);
            return true;
        }
        final double banknoteValue = dataContainer.get(namespacedKey, PersistentDataType.DOUBLE);

        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return true;
        }

        if (banknotesCount < 0) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.DEPOSIT_NEGATIVE_AMOUNT);
            return true;
        }

        if (banknotesCount > itemInHand.getAmount()) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.DEPOSIT_INVALID_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, banknotesCount));
            return true;
        }

        final var depositValue = banknoteValue * banknotesCount;
        final var maxBalance = this.economyConfig.getDouble("money.max");
        if (this.economy.getBalance((Player) commandSender) + depositValue > maxBalance) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.DEPOSIT_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, this.economy.format(maxBalance)));
            return true;
        }

        this.economy.depositPlayer((Player) commandSender, depositValue);
        itemInHand.setAmount(itemInHand.getAmount() - banknotesCount);

        ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.DEPOSIT_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(depositValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) commandSender))));

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
            final String alias, final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("amount");
        }

        return completions;
    }

}
