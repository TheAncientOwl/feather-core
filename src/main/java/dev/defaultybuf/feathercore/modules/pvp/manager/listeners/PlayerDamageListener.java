/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDamageListener.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Put players in combat on damage event
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import dev.defaultybuf.feather.toolkit.api.FeatherListener;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PlayerDamageListener extends FeatherListener {
    public PlayerDamageListener(final InitData data) {
        super(data);
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

        getInterface(IPvPManager.class).putPlayersInCombat((Player) victimEntity, damager);
    }
}
