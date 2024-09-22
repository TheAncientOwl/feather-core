package mc.owls.valley.net.feathercore.modules.loot.chests.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.TimeUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.module.interfaces.ILootChestsModule;
import mc.owls.valley.net.feathercore.modules.loot.chests.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class LootChestOpenListener implements IFeatherListener {
    private TranslationManager lang = null;
    private ILootChestsModule lootChests = null;
    private IPropertyAccessor config = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.lang = core.getTranslationManager();
        this.lootChests = core.getLootChestsModule();
        this.config = core.getConfigurationManager().getLootChestsConfigFile();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestOpen(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CHEST) {
            return;
        }

        final String chestLocation = block.getLocation().toString();

        final String chestType = this.lootChests.getChestType(chestLocation);
        if (chestType == null) {
            return;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final Long openChestTime = this.lootChests.getOpenChestTime(player, chestLocation);

        final var now = System.currentTimeMillis();
        if (openChestTime != null
                && openChestTime + this.config.getLong("chests." + chestType + ".cooldown") * 1000 > now
                && !player.hasPermission("feathercore.lootchests.bypass-cooldown")) {
            this.lang.message(player, Message.COOLDOWN,
                    Pair.of(Placeholder.COOLDOWN, TimeUtils.formatDuration(openChestTime, now)));
            return;
        }

        this.lootChests.openChest(player, chestType, chestLocation, now);
    }

}
