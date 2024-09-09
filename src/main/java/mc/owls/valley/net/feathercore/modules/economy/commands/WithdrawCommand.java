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

import mc.owls.valley.net.feathercore.api.common.ChatUtils;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;

public class WithdrawCommand implements IFeatherCommand {
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
        if (!commandSender.hasPermission("feathercore.economy.general.withdraw")) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 2) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.USAGE_INVALID, Message.USAGE_WITHDRAW);
            return true;
        }

        double banknoteValue = 0;
        try {
            banknoteValue = Double.parseDouble(args[0]);
        } catch (final Exception e) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[0]));
            return true;
        }

        int banknotesCount = 0;
        try {
            banknotesCount = Integer.parseInt(args[1]);
        } catch (final Exception e) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.NOT_VALID_NUMBER,
                    Pair.of(Placeholder.STRING, args[1]));
            return true;
        }

        final var minWithdraw = Math.max(0, this.economyConfig.getDouble("banknote.minimum-value"));
        if (banknoteValue < minWithdraw) {
            ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.WITHDRAW_MIN_AMOUNT,
                    Pair.of(Placeholder.MIN, this.economy.format(minWithdraw)));
            return true;
        }

        final var withdrawValue = banknoteValue * banknotesCount;

        if (!this.economy.has((Player) commandSender, withdrawValue)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.WITHDRAW_NO_FUNDS);
            return true;
        }

        final ItemStack banknote = makeBanknotes(commandSender, banknoteValue, banknotesCount,
                this.messages.getStringList(Message.BANKNOTE_LORE));

        if (!canAddBanknote((Player) commandSender, banknote)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.WITHDRAW_NO_SPACE);
            return true;
        }

        this.economy.withdrawPlayer((Player) commandSender, withdrawValue);
        ((Player) commandSender).getInventory().addItem(banknote);

        ChatUtils.sendPlaceholderMessage(commandSender, this.messages, Message.WITHDRAW_SUCCESS,
                Pair.of(Placeholder.AMOUNT, this.economy.format(withdrawValue)),
                Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) commandSender))));

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
            final String alias, final String[] args) {
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
    private ItemStack makeBanknotes(
            final CommandSender commandSender, final double banknoteValue, final int banknotesCount,
            final List<String> lore) {
        // 1. create item stack
        final Material material = Material.getMaterial(this.economyConfig.getString("banknote.material"));
        ItemStack banknote = null;

        try {
            banknote = new ItemStack(material);
        } catch (final IllegalArgumentException e) {
            banknote = new ItemStack(Material.PAPER);
            ChatUtils.sendMessage(commandSender, this.messages, Message.BANKNOTE_INVALID_MATERIAL);
        }

        // 2. check lore for {amount} placeholder
        if (!lore.stream().anyMatch(line -> line.contains(Placeholder.AMOUNT))) {
            lore.add("&8Value: &a{amount}");
        }

        // 3. setup item meta
        final ItemMeta meta = banknote.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(this.messages.getString(Message.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line, Pair.of(Placeholder.AMOUNT, banknoteValue))))
                .toList());
        meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, Message.BANKNOTE_METADATA_KEY),
                PersistentDataType.DOUBLE, banknoteValue);

        // 4. finish itemstack setup
        banknote.setItemMeta(meta);
        banknote.setAmount(banknotesCount);
        banknote.addUnsafeEnchantment(Enchantment.INFINITY, 1);

        return banknote;
    }

}
