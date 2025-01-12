/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPManager.java
 * @author Alexandru Delegeanu
 * @version 0.11
 * @description Module responsible for managing pvp restrictions
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.Clock;
import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PvPManager extends FeatherModule implements IPvPManager {
    private final Map<UUID, Long> playersInCombat = new HashMap<>();
    @SuppressWarnings("unused") private BukkitTask combatCheckTask = null;

    public PvPManager(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        this.combatCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                getPlugin(), new CombatChecker(this), 0,
                this.config.getTicks("combat.check-interval"));
    }

    @Override
    protected void onModuleDisable() {
        this.playersInCombat.clear();
    }

    /**
     * Check if given player is tagged in combat.
     * 
     * @param player
     * @return true if the player is tagged in combat, false otherwise
     */
    @Override
    public boolean isPlayerInCombat(final Player player) {
        return this.playersInCombat.containsKey(player.getUniqueId());
    }

    /**
     * Check if given player is tagged in combat.
     * 
     * @param uuid of the player
     * @return true if the player is tagged in combat, false otherwise
     */
    @Override
    public boolean isPlayerInCombat(final UUID uuid) {
        return this.playersInCombat.containsKey(uuid);
    }

    /**
     * Tags given players in combat.
     * 
     * @param victim player who was damaged
     * @param attacker player who damaged
     */
    @Override
    public void putPlayersInCombat(final Player victim, final Player attacker) {
        if (victim.getUniqueId().equals(attacker.getUniqueId())) {
            return;
        }

        final var currentTime = Clock.currentTimeMillis();
        putPlayerInCombat(attacker, victim.getName(), currentTime, Message.PvPManager.TAG);
        putPlayerInCombat(victim, attacker.getName(), currentTime, Message.PvPManager.TAGGED);
    }

    /**
     * Put player in combat if not already, toggle fly and send message
     * 
     * @see PvPManager.putPlayersInCombat(victim, attacker)
     * @param player
     * @param otherName
     * @param currentTime
     * @param messageKey
     */
    private void putPlayerInCombat(final Player player, final String otherName,
            final long currentTime, final String messageKey) {
        if (!this.playersInCombat.containsKey(player.getUniqueId())) {
            getLanguage().message(player, messageKey,
                    Pair.of(Placeholder.PLAYER, otherName));
            if (!player.hasPermission("pvp.bypass.fly")) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }
        this.playersInCombat.put(player.getUniqueId(), currentTime);
    }

    /**
     * Remove combat tag of a player.
     * 
     * @param player
     */
    @Override
    public void removePlayerInCombat(final Player player) {
        this.playersInCombat.remove(player.getUniqueId());
        getLanguage().message(player, Message.PvPManager.COMBAT_ENDED);
    }

    /**
     * Remove combat tag of a player.
     * 
     * @param uuid of the player
     */
    @Override
    public void removePlayerInCombat(final UUID uuid) {
        this.playersInCombat.remove(uuid);
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            getLanguage().message(player, Message.PvPManager.COMBAT_ENDED);
        }
    }

    /**
     * @return list containing all whitelisted commands during combat
     */
    @Override
    public List<String> getWhitelistedCommands() {
        return this.config.getStringList("commands.whitelist");
    }

    public static final class CombatChecker implements Runnable {
        final PvPManager pvpManager;

        public CombatChecker(final PvPManager pvpManager) {
            super();
            this.pvpManager = pvpManager;
        }

        /**
         * Remove players whose combat timer has expired
         */
        @Override
        public void run() {
            final var currentTime = Clock.currentTimeMillis();

            final var combatTime = this.pvpManager.config.getMillis("combat.time");
            final var iterator = this.pvpManager.playersInCombat.entrySet().iterator();
            while (iterator.hasNext()) {
                final var entry = iterator.next();
                if (entry.getValue() + combatTime < currentTime) {
                    iterator.remove();
                    this.pvpManager.removePlayerInCombat(entry.getKey());
                }
            }
        }
    }
}
