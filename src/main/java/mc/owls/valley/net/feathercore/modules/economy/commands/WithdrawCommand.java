/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file WithdrawCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Withdraw banknotes from player's balance
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomy;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class WithdrawCommand extends FeatherCommand<WithdrawCommand.CommandData> {
    public WithdrawCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(ItemStack banknote, double withdrawValue) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.withdraw")) {
            getLanguage().message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final var economy = getInterface(IFeatherEconomy.class).getEconomy();

        economy.withdrawPlayer((Player) sender, data.withdrawValue);
        ((Player) sender).getInventory().addItem(data.banknote);

        getLanguage().message(sender, Message.Economy.WITHDRAW_SUCCESS, List.of(
                Pair.of(Placeholder.AMOUNT, economy.format(data.withdrawValue)),
                Pair.of(Placeholder.BALANCE, economy.format(economy.getBalance((Player) sender)))));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. basic checks
        if (!(sender instanceof Player)) {
            getLanguage().message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        if (args.length != 2) {
            getLanguage().message(sender, Message.General.USAGE_INVALID,
                    Message.Economy.USAGE_WITHDRAW);
            return null;
        }

        // 2. parse banknote value
        double banknoteValue = 0;
        try {
            banknoteValue = Double.parseDouble(args[0]);
        } catch (final Exception e) {
            getLanguage().message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        // 3. parse banknotes count
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            getLanguage().message(sender, Message.General.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return null;
        }

        // 4. check if withdraw data is valid
        final var minWithdraw = Math.max(0,
                getInterface(IFeatherEconomy.class).getConfig()
                        .getDouble("banknote.minimum-value"));
        if (banknoteValue < minWithdraw) {
            getLanguage().message(sender, Message.Economy.WITHDRAW_MIN_AMOUNT,
                    Pair.of(Placeholder.MIN,
                            getInterface(IFeatherEconomy.class).getEconomy().format(minWithdraw)));
            return null;
        }

        final var withdrawValue = banknoteValue * banknotesCount;

        if (!getInterface(IFeatherEconomy.class).getEconomy().has((Player) sender, withdrawValue)) {
            getLanguage().message(sender, Message.Economy.WITHDRAW_NO_FUNDS);
            return null;
        }

        // 5. create banknote and check if it can be added to player's inventory
        final ItemStack banknote = makeBanknotes(sender, banknoteValue, banknotesCount,
                getLanguage().getTranslation(sender).getStringList(Message.Economy.BANKNOTE_LORE));

        if (!canAddBanknote((Player) sender, banknote)) {
            getLanguage().message(sender, Message.Economy.WITHDRAW_NO_SPACE);
            return null;
        }

        return new CommandData(banknote, withdrawValue);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("banknote-value");
        } else if (args.length == 2) {
            completions.add("amount");
        }

        return completions;
    }

    public boolean canAddBanknote(final Player player, final ItemStack banknote) {
        final ItemStack[] items = player.getInventory().getContents();
        final var targetAmount = banknote.getAmount();
        final var STACK_SIZE = 64;

        int freeSpace = 0;
        // @note 36 boots, 37 leggings, 38 chestplate, 39 helmet, 40 off hand
        for (int index = 0; index < 36; ++index) {
            if (items[index] == null) {
                freeSpace += STACK_SIZE;
            } else if (items[index].isSimilar(banknote) && items[index].getAmount() < STACK_SIZE) {
                freeSpace += (STACK_SIZE - items[index].getAmount());
            }
            if (targetAmount <= freeSpace) {
                return true;
            }
        }
        return false;
    }

    private ItemStack makeBanknotes(final CommandSender sender,
            final double banknoteValue, final int banknotesCount, final List<String> lore) {
        // 1. create item stack
        final Material material = Material
                .getMaterial(getInterface(IFeatherEconomy.class).getConfig()
                        .getString("banknote.material"));
        ItemStack banknote = null;

        try {
            banknote = new ItemStack(material);
        } catch (final IllegalArgumentException e) {
            banknote = new ItemStack(Material.PAPER);
            getLanguage().message(sender, Message.Economy.BANKNOTE_INVALID_MATERIAL);
        }

        // 2. check lore for {amount} placeholder
        if (!lore.stream().anyMatch(line -> line.contains(Placeholder.AMOUNT))) {
            lore.add("&8Value: &a{amount}");
        }

        // 3. setup item meta
        final ItemMeta meta = banknote.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(
                        getLanguage().getTranslation(sender)
                                .getString(Message.Economy.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line,
                                List.of(Pair.of(Placeholder.AMOUNT, banknoteValue)))))
                .toList());
        new NamespacedKey(getPlugin(), meta,
                getInterface(IFeatherEconomy.class).getConfig().getString("banknote.key"))
                        .set(PersistentDataType.DOUBLE, banknoteValue);

        // 4. finish itemstack setup
        banknote.setItemMeta(meta);
        banknote.setAmount(banknotesCount);
        banknote.addUnsafeEnchantment(Enchantment.INFINITY, 1);

        return banknote;
    }

}
