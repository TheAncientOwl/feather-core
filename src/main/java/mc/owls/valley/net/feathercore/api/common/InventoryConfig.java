package mc.owls.valley.net.feathercore.api.common;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class InventoryConfig {
    public static void serialize(final IPropertyAccessor config, final String path, final Inventory inventory) {
        final var inventorySize = inventory.getSize();
        config.setInt(path + ".size", inventorySize);

        for (int index = 0; index < inventorySize; index++) {
            final ItemStack itemStack = inventory.getItem(index);
            if (itemStack != null) {
                config.setItemStack(path + ".content." + index, itemStack);
            }
        }
    }

    public static Inventory deserialize(final IPropertyAccessor inventoryConfig) {
        if (inventoryConfig == null) {
            return null;
        }

        try {
            final var inventorySize = inventoryConfig.getInt("size");
            Inventory inventory = Bukkit.createInventory(null, inventorySize,
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            inventoryConfig.getString("display-name")));

            final IPropertyAccessor content = inventoryConfig.getConfigurationSection("content");
            for (final var slot : content.getKeys(false)) {
                final var itemStack = content.getItemStack(slot);
                inventory.setItem(Integer.parseInt(slot), itemStack);
            }

            return inventory;
        } catch (final Exception e) {
            return null;
        }
    }
}
