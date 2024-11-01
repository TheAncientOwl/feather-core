/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDamageListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Put players in combat on damage event
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PlayerDamageListener implements IFeatherListener {
    private IPvPManager pvpManager = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getPvPManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        final Entity victimEntity = event.getEntity();

        if (event.isCancelled() || event.getDamage() == 0 || !(victimEntity instanceof Player)) {
            return;
        }

        final Entity damagerEntity = event.getDamager();
        Player damager = null;
        if (damagerEntity instanceof Player) {
            damager = (Player) damagerEntity;
        } else if (damagerEntity instanceof Projectile) {
            final Projectile projectile = (Projectile) damagerEntity;
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            } else {
                return;
            }
        } else {
            return;
        }

        this.pvpManager.putPlayersInCombat((Player) victimEntity, damager);
    }
}
