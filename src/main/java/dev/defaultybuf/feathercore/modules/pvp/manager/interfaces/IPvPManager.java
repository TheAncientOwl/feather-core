/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IPvPManager.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description PvPManager module interface
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.interfaces;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev.defaultybuf.feather.toolkit.api.interfaces.IConfigAccessor;

public interface IPvPManager extends IConfigAccessor {
    /**
     * Check if given player is tagged in combat.
     * 
     * @param player
     * @return true if the player is tagged in combat, false otherwise
     */
    public boolean isPlayerInCombat(final Player player);

    /**
     * Check if given player is tagged in combat.
     * 
     * @param uuid of the player
     * @return true if the player is tagged in combat, false otherwise
     */
    public boolean isPlayerInCombat(final UUID uuid);

    /**
     * Tags given players in combat.
     * 
     * @param victim player who was damaged
     * @param attacker player who damaged
     */
    public void putPlayersInCombat(final Player victim, final Player attacker);

    /**
     * Remove combat tag of a player.
     * 
     * @param player
     */
    public void removePlayerInCombat(final Player player);

    /**
     * Remove combat tag of a player.
     * 
     * @param uuid of the player
     */
    public void removePlayerInCombat(final UUID uuid);

    /**
     * @return list containing all whitelisted commands during combat
     */
    public List<String> getWhitelistedCommands();
}
