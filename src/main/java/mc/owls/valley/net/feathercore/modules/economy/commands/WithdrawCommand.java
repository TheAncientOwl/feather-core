package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;

public class WithdrawCommand implements IFeatherCommand {
    private static record CommandData(ItemStack banknote, double withdrawValue) {
    }

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
        final CommandData data = parse(sender, args);

        if (data == null) {
            return true;
        }

        this.economy.withdrawPlayer((Player) sender, data.withdrawValue);
        ((Player) sender).getInventory().addItem(data.banknote);

        Message.to(sender, this.messages, Messages.WITHDRAW_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(data.withdrawValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
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

    @SuppressWarnings("unchecked")
    private ItemStack makeBanknotes(final CommandSender sender,
            final double banknoteValue, final int banknotesCount, final List<String> lore) {
        // 1. create item stack
        final Material material = Material.getMaterial(this.economyConfig.getString("banknote.material"));
        ItemStack banknote = null;

        try {
            banknote = new ItemStack(material);
        } catch (final IllegalArgumentException e) {
            banknote = new ItemStack(Material.PAPER);
            Message.to(sender, this.messages, Messages.BANKNOTE_INVALID_MATERIAL);
        }

        // 2. check lore for {amount} placeholder
        if (!lore.stream().anyMatch(line -> line.contains(Placeholder.AMOUNT))) {
            lore.add("&8Value: &a{amount}");
        }

        // 3. setup item meta
        final ItemMeta meta = banknote.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(this.messages.getString(Messages.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line, Pair.of(Placeholder.AMOUNT, banknoteValue))))
                .toList());
        meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, Messages.BANKNOTE_METADATA_KEY),
                PersistentDataType.DOUBLE, banknoteValue);

        // 4. finish itemstack setup
        banknote.setItemMeta(meta);
        banknote.setAmount(banknotesCount);
        banknote.addUnsafeEnchantment(Enchantment.INFINITY, 1);

        return banknote;
    }

    @SuppressWarnings("unchecked")
    private CommandData parse(final CommandSender sender, final String[] args) {
        // 1. basic checks
        if (!sender.hasPermission("feathercore.economy.general.withdraw")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 2) {
            Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_WITHDRAW);
            return null;
        }

        // 2. parse banknote value
        double banknoteValue = 0;
        try {
            banknoteValue = Double.parseDouble(args[0]);
        } catch (final Exception e) {
            Message.to(sender, this.messages, Messages.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return null;
        }

        // 3. parse banknotes count
        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            Message.to(sender, this.messages, Messages.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return null;
        }

        // 4. check if withdraw data is valid
        final var minWithdraw = Math.max(0, this.economyConfig.getDouble("banknote.minimum-value"));
        if (banknoteValue < minWithdraw) {
            Message.to(sender, this.messages, Messages.WITHDRAW_MIN_AMOUNT,
                    Pair.of(Placeholder.MIN, this.economy.format(minWithdraw)));
            return null;
        }

        final var withdrawValue = banknoteValue * banknotesCount;

        if (!this.economy.has((Player) sender, withdrawValue)) {
            Message.to(sender, this.messages, Messages.WITHDRAW_NO_FUNDS);
            return null;
        }

        // 5. create banknote and check if it can be added to player's inventory
        final ItemStack banknote = makeBanknotes(sender, banknoteValue, banknotesCount,
                this.messages.getStringList(Messages.BANKNOTE_LORE));

        if (!canAddBanknote((Player) sender, banknote)) {
            Message.to(sender, this.messages, Messages.WITHDRAW_NO_SPACE);
            return null;
        }

        return new CommandData(banknote, withdrawValue);
    }

}
