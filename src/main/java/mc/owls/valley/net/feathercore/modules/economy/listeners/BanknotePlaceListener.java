/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePlaceListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Prevent player from placing blocks representing banknotes
 */

package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;

public class BanknotePlaceListener implements IFeatherListener {
    private JavaPlugin plugin = null;
    private IConfigFile economyConfig = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.plugin = core.getPlugin();
        this.economyConfig = core.getFeatherEconomy().getConfig();
        this.lang = core.getLanguageManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final var itemMeta = event.getItemInHand().getItemMeta();
        if (itemMeta != null
                && new NamespacedKey(this.plugin, itemMeta, this.economyConfig.getString("banknote.key")).isPresent()) {
            event.setCancelled(true);
            this.lang.message(event.getPlayer(), Message.BANKNOTE_PLACE);
        }

    }

}
