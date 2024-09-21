package mc.owls.valley.net.feathercore.api.module.interfaces;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public interface ILootChestsModule {
    public void addChest(final Location location, final String type);

    public Inventory getChestInventory(final Location location);
}
