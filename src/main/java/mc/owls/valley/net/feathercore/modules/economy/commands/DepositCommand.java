/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DepositCommand.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Deposit banknotes to player's balance
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.milkbowl.vault.economy.Economy;

public class DepositCommand extends FeatherCommand<DepositCommand.CommandData> {
    public static record CommandData(ItemStack itemInHand, int banknotesCount, double depositValue) {
    }

    private Economy economy = null;
    private JavaPlugin plugin = null;
    private TranslationManager lang = null;
    private IPropertyAccessor economyConfig = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.plugin = core.getPlugin();
        this.economy = core.getEconomy();
        this.economyConfig = core.getFeatherEconomy().getConfig();
        this.lang = core.getTranslationManager();
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        this.economy.depositPlayer((Player) sender, data.depositValue);
        data.itemInHand.setAmount(data.itemInHand.getAmount() - data.banknotesCount);

        this.lang.message(sender, Message.DEPOSIT_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(data.depositValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));
    }

    public CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check for basics
        if (!sender.hasPermission("feathercore.economy.general.deposit")) {
            this.lang.message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            this.lang.message(sender, Message.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 1) {
            this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_DEPOSIT);
            return null;
        }

        // 2. check if item in hand is valid banknote and get the banknote value
        final ItemStack itemInHand = ((Player) sender).getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null) {
            this.lang.message(sender, Message.BANKNOTE_INVALID);
            return null;
        }

        final var namespacedKey = new NamespacedKey(this.plugin, itemInHand.getItemMeta(),
                this.economyConfig.getString("banknote.key"));
        if (!namespacedKey.isPresent()) {
            this.lang.message(sender, Message.BANKNOTE_INVALID);
            return null;
        }
        final double banknoteValue = namespacedKey.get(PersistentDataType.DOUBLE);

        // 3. parse the banknotes count and validate
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            this.lang.message(sender, Message.NOT_VALID_NUMBER, Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        if (banknotesCount < 0) {
            this.lang.message(sender, Message.DEPOSIT_NEGATIVE_AMOUNT);
            return null;
        }

        if (banknotesCount > itemInHand.getAmount()) {
            this.lang.message(sender, Message.DEPOSIT_INVALID_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, banknotesCount));
            return null;
        }

        // 4. check for max deposit value
        final var depositValue = banknoteValue * banknotesCount;
        final var maxBalance = this.economyConfig.getDouble("balance.max");
        if (this.economy.getBalance((Player) sender) + depositValue > maxBalance) {
            this.lang.message(sender, Message.DEPOSIT_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, this.economy.format(maxBalance)));
            return null;
        }

        return new CommandData(itemInHand, banknotesCount, depositValue);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("amount");
        }

        return completions;
    }

}
