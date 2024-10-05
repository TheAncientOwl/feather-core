/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file WithdrawCommand.java
 * @author Alexandru Delegeanu
 * @version 0.3
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
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;

public class WithdrawCommand extends FeatherCommand<WithdrawCommand.CommandData> {
    public static record CommandData(ItemStack banknote, double withdrawValue) {
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
        this.economy.withdrawPlayer((Player) sender, data.withdrawValue);
        ((Player) sender).getInventory().addItem(data.banknote);

        this.lang.message(sender, Message.WITHDRAW_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(data.withdrawValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        // 1. basic checks
        if (!sender.hasPermission("feathercore.economy.general.withdraw")) {
            this.lang.message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            this.lang.message(sender, Message.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 2) {
            this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_WITHDRAW);
            return null;
        }

        // 2. parse banknote value
        double banknoteValue = 0;
        try {
            banknoteValue = Double.parseDouble(args[0]);
        } catch (final Exception e) {
            this.lang.message(sender, Message.NOT_VALID_NUMBER, Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        // 3. parse banknotes count
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            this.lang.message(sender, Message.NOT_VALID_NUMBER, Pair.of(Placeholder.STRING, args[1]));
            return null;
        }

        // 4. check if withdraw data is valid
        final var minWithdraw = Math.max(0, this.economyConfig.getDouble("banknote.minimum-value"));
        if (banknoteValue < minWithdraw) {
            this.lang.message(sender, Message.WITHDRAW_MIN_AMOUNT,
                    Pair.of(Placeholder.MIN, this.economy.format(minWithdraw)));
            return null;
        }

        final var withdrawValue = banknoteValue * banknotesCount;

        if (!this.economy.has((Player) sender, withdrawValue)) {
            this.lang.message(sender, Message.WITHDRAW_NO_FUNDS);
            return null;
        }

        // 5. create banknote and check if it can be added to player's inventory
        final ItemStack banknote = makeBanknotes(sender, banknoteValue, banknotesCount,
                this.lang.getTranslation(sender).getStringList(Message.BANKNOTE_LORE));

        if (!canAddBanknote((Player) sender, banknote)) {
            this.lang.message(sender, Message.WITHDRAW_NO_SPACE);
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
        final Material material = Material.getMaterial(this.economyConfig.getString("banknote.material"));
        ItemStack banknote = null;

        try {
            banknote = new ItemStack(material);
        } catch (final IllegalArgumentException e) {
            banknote = new ItemStack(Material.PAPER);
            this.lang.message(sender, Message.BANKNOTE_INVALID_MATERIAL);
        }

        // 2. check lore for {amount} placeholder
        if (!lore.stream().anyMatch(line -> line.contains(Placeholder.AMOUNT))) {
            lore.add("&8Value: &a{amount}");
        }

        // 3. setup item meta
        final ItemMeta meta = banknote.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(this.lang.getTranslation(sender).getString(Message.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line, Pair.of(Placeholder.AMOUNT, banknoteValue))))
                .toList());
        new NamespacedKey(this.plugin, meta, this.economyConfig.getString("banknote.key"))
                .set(PersistentDataType.DOUBLE, banknoteValue);

        // 4. finish itemstack setup
        banknote.setItemMeta(meta);
        banknote.setAmount(banknotesCount);
        banknote.addUnsafeEnchantment(Enchantment.INFINITY, 1);

        return banknote;
    }

}
