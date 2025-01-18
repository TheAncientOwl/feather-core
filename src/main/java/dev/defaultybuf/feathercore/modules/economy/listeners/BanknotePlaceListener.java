/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePlaceListener.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Prevent player from placing blocks representing banknotes
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import dev.defaultybuf.feather.toolkit.api.FeatherListener;
import dev.defaultybuf.feathercore.common.Message;
import dev.defaultybuf.feathercore.common.minecraft.NamespacedKey;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;

public class BanknotePlaceListener extends FeatherListener {
    public BanknotePlaceListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final var itemMeta = event.getItemInHand().getItemMeta();
        if (itemMeta != null
                && new NamespacedKey(getPlugin(), itemMeta,
                        getInterface(IFeatherEconomy.class).getConfig().getString("banknote.key"))
                                .isPresent()) {
            event.setCancelled(true);
            getLanguage().message(event.getPlayer(), Message.Economy.BANKNOTE_PLACE);
        }

    }
}
