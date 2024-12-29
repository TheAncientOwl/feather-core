/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDeathListener.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Remove player in combat on death
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PlayerDeathListener extends FeatherListener {
    public PlayerDeathListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }

        getInterface(IPvPManager.class).removePlayerInCombat(event.getPlayer());
    }
}
