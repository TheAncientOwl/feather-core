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

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;
import net.milkbowl.vault.economy.Economy;

public class DepositCommand implements IFeatherCommand {
    private Economy economy = null;
    private JavaPlugin plugin = null;
    private IPropertyAccessor messages = null;
    private IPropertyAccessor economyConfig = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.plugin = core.getPlugin();
        this.economy = core.getEconomy();
        this.economyConfig = core.getConfigurationManager().getEconomyConfigFile();
        this.messages = core.getConfigurationManager().getMessagesConfigFile()
                .getConfigurationSection(Messages.ECONOMY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("feathercore.economy.general.deposit")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return true;
        }

        if (!(sender instanceof Player)) {
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 1) {
            Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_DEPOSIT);
            return true;
        }

        final ItemStack itemInHand = ((Player) sender).getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null
                || itemInHand.getItemMeta().getPersistentDataContainer() == null) {
            Message.to(sender, this.messages, Messages.BANKNOTE_INVALID);
            return true;
        }

        final NamespacedKey namespacedKey = new NamespacedKey(this.plugin, Messages.BANKNOTE_METADATA_KEY);
        final PersistentDataContainer dataContainer = itemInHand.getItemMeta().getPersistentDataContainer();
        if (!dataContainer.has(new NamespacedKey(this.plugin, Messages.BANKNOTE_METADATA_KEY))) {
            Message.to(sender, this.messages, Messages.BANKNOTE_INVALID);
            return true;
        }
        final double banknoteValue = dataContainer.get(namespacedKey, PersistentDataType.DOUBLE);

        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            Message.to(sender, this.messages, Messages.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return true;
        }

        if (banknotesCount < 0) {
            Message.to(sender, this.messages, Messages.DEPOSIT_NEGATIVE_AMOUNT);
            return true;
        }

        if (banknotesCount > itemInHand.getAmount()) {
            Message.to(sender, this.messages, Messages.DEPOSIT_INVALID_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, banknotesCount));
            return true;
        }

        final var depositValue = banknoteValue * banknotesCount;
        final var maxBalance = this.economyConfig.getDouble("money.max");
        if (this.economy.getBalance((Player) sender) + depositValue > maxBalance) {
            Message.to(sender, this.messages, Messages.DEPOSIT_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, this.economy.format(maxBalance)));
            return true;
        }

        this.economy.depositPlayer((Player) sender, depositValue);
        itemInHand.setAmount(itemInHand.getAmount() - banknotesCount);

        Message.to(sender, this.messages, Messages.DEPOSIT_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(depositValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("amount");
        }

        return completions;
    }

}
