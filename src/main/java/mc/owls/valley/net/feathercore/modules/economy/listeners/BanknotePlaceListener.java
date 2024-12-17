/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePlaceListener.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Prevent player from placing blocks representing banknotes
 */

package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.core.FeatherListener;
import mc.owls.valley.net.feathercore.modules.economy.interfaces.IFeatherEconomyProvider;

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
                        getInterface(IFeatherEconomyProvider.class).getConfig().getString("banknote.key"))
                        .isPresent()) {
            event.setCancelled(true);
            getLanguage().message(event.getPlayer(), Message.Economy.BANKNOTE_PLACE);
        }

    }
}
