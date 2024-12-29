/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestOpenListener.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @description Open config loot-chest on interact
 */

package dev.defaultybuf.feathercore.modules.loot.chests.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.TimeUtils;
import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.loot.chests.interfaces.ILootChests;

public class LootChestOpenListener extends FeatherListener {
    public LootChestOpenListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestOpen(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CHEST) {
            return;
        }

        final String chestLocation = block.getLocation().toString();

        final String chestType = getInterface(ILootChests.class).getChestType(chestLocation);
        if (chestType == null) {
            return;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final Long openChestTime =
                getInterface(ILootChests.class).getOpenChestTime(player, chestLocation);

        final var now = System.currentTimeMillis();
        final var cooldown = getInterface(ILootChests.class).getConfig()
                .getMillis("chests." + chestType + ".cooldown");
        if (openChestTime != null
                && openChestTime + cooldown > now
                && !player.hasPermission("feathercore.lootchests.bypass-cooldown")) {
            getLanguage().message(player, Message.LootChests.COOLDOWN,
                    Pair.of(Placeholder.COOLDOWN,
                            TimeUtils.formatRemaining(openChestTime, cooldown)));
            return;
        }

        getInterface(ILootChests.class).openChest(player, chestType, chestLocation, now);
    }

}
