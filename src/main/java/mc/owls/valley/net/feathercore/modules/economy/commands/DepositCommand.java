/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DepositCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Deposit banknotes to player's balance
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.core.interfaces.IPluginProvider;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomyProvider;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

public class DepositCommand extends FeatherCommand<DepositCommand.CommandData> {
    public DepositCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(ItemStack itemInHand, int banknotesCount, double depositValue) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.deposit")) {
            getInterface(ILanguage.class).message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        getInterface(IFeatherEconomyProvider.class).getEconomy().depositPlayer((Player) sender, data.depositValue);
        data.itemInHand.setAmount(data.itemInHand.getAmount() - data.banknotesCount);

        getInterface(ILanguage.class).message(sender, Message.Economy.DEPOSIT_SUCCESS, List.of(
                Pair.of(Placeholder.AMOUNT,
                        getInterface(IFeatherEconomyProvider.class).getEconomy().format(data.depositValue)),
                Pair.of(Placeholder.BALANCE, getInterface(IFeatherEconomyProvider.class).getEconomy()
                        .format(getInterface(IFeatherEconomyProvider.class).getEconomy()
                                .getBalance((Player) sender)))));
    }

    public CommandData parse(final CommandSender sender, final String[] args) {
        // 1. check for basics
        if (!(sender instanceof Player)) {
            getInterface(ILanguage.class).message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        if (args.length != 1) {
            getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.Economy.USAGE_DEPOSIT);
            return null;
        }

        // 2. check if item in hand is valid banknote and get the banknote value
        final ItemStack itemInHand = ((Player) sender).getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null) {
            getInterface(ILanguage.class).message(sender, Message.Economy.BANKNOTE_INVALID);
            return null;
        }

        final var namespacedKey = new NamespacedKey(getInterface(IPluginProvider.class).getPlugin(),
                itemInHand.getItemMeta(),
                getInterface(IFeatherEconomyProvider.class).getConfig().getString("banknote.key"));
        if (!namespacedKey.isPresent()) {
            getInterface(ILanguage.class).message(sender, Message.Economy.BANKNOTE_INVALID);
            return null;
        }
        final double banknoteValue = namespacedKey.get(PersistentDataType.DOUBLE);

        // 3. parse the banknotes count and validate
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            getInterface(ILanguage.class).message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        if (banknotesCount < 0) {
            getInterface(ILanguage.class).message(sender, Message.Economy.DEPOSIT_NEGATIVE_AMOUNT);
            return null;
        }

        if (banknotesCount > itemInHand.getAmount()) {
            getInterface(ILanguage.class).message(sender, Message.Economy.DEPOSIT_INVALID_AMOUNT,
                    Pair.of(Placeholder.AMOUNT, banknotesCount));
            return null;
        }

        // 4. check for max deposit value
        final var depositValue = banknoteValue * banknotesCount;
        final var maxBalance = getInterface(IFeatherEconomyProvider.class).getConfig().getDouble("balance.max");
        if (getInterface(IFeatherEconomyProvider.class).getEconomy().getBalance((Player) sender)
                + depositValue > maxBalance) {
            getInterface(ILanguage.class).message(sender, Message.Economy.DEPOSIT_BALANCE_EXCEEDS,
                    Pair.of(Placeholder.MAX,
                            getInterface(IFeatherEconomyProvider.class).getEconomy().format(maxBalance)));
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
