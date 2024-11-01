/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDeathListener.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Remove player in combat on death
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.pvp.manager.components.PvPManager;

public class PlayerDeathListener implements IFeatherListener {
    private PvPManager pvpManager = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getPvPManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.pvpManager.removePlayerInCombat(event.getPlayer());
    }

}
