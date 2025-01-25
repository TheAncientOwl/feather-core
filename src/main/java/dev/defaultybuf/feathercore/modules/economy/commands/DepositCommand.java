/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DepositCommand.java
 * @author Alexandru Delegeanu
 * @version 0.10
 * @description Deposit banknotes to player's balance
 */

package dev.defaultybuf.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import dev.defaultybuf.feather.toolkit.api.FeatherCommand;
import dev.defaultybuf.feather.toolkit.util.java.Pair;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.common.minecraft.NamespacedKey;
import dev.defaultybuf.feathercore.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

public class DepositCommand extends FeatherCommand<DepositCommand.CommandData> {
    public DepositCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(ItemStack itemInHand, int banknotesCount,
            double depositValue) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.deposit")) {
            getLanguage().message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();
        economy.depositPlayer((Player) sender, data.depositValue);
        data.itemInHand.setAmount(data.itemInHand.getAmount() - data.banknotesCount);

        getLanguage().message(sender, Message.Economy.DEPOSIT_SUCCESS, List.of(
                Pair.of(Placeholder.AMOUNT, economy.format(data.depositValue)),
                Pair.of(Placeholder.BALANCE, economy.format(economy.getBalance((Player) sender)))));
    }

    public CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check for basics
        if (!(sender instanceof Player)) {
            getLanguage().message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        if (args.length != 1) {
            getLanguage().message(sender, Message.General.USAGE_INVALID,
                    Message.Economy.USAGE_DEPOSIT);
            return null;
        }

        // 2. check if item in hand is valid banknote and get the banknote value
        final ItemStack itemInHand = ((Player) sender).getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null) {
            getLanguage().message(sender, Message.Economy.BANKNOTE_INVALID);
            return null;
        }

        final var economyConfig = getInterface(IFeatherEconomy.class).getConfig();

        final var namespacedKey = new NamespacedKey(getPlugin(), itemInHand.getItemMeta(),
                economyConfig.getString("banknote.key"));
        if (!namespacedKey.isPresent()) {
            getLanguage().message(sender, Message.Economy.BANKNOTE_INVALID);
            return null;
        }
        final double banknoteValue = namespacedKey.get(PersistentDataType.DOUBLE);

        // 3. parse the banknotes count and validate
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            getLanguage().message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        if (banknotesCount < 0) {
            getLanguage().message(sender, Message.Economy.DEPOSIT_NEGATIVE_AMOUNT);
            return null;
        }

        if (banknotesCount > itemInHand.getAmount()) {
            getLanguage().message(sender, Message.Economy.DEPOSIT_INVALID_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, banknotesCount));
            return null;
        }

        // 4. check for max deposit value
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();
        final var depositValue = banknoteValue * banknotesCount;
        final var maxBalance = economyConfig.getDouble("balance.max");
        if (economy.getBalance((Player) sender) + depositValue > maxBalance) {
            getLanguage().message(sender, Message.Economy.DEPOSIT_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX, economy.format(maxBalance)));
            return null;
        }

        return new CommandData(itemInHand, banknotesCount, depositValue);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        completions.add("amount");

        return completions;
    }

}
