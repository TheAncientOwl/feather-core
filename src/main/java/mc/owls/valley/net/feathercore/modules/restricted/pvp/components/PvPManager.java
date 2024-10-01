/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPManager.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Module responsible for managing pvp restrictions
 */

package mc.owls.valley.net.feathercore.modules.restricted.pvp.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigFile;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Message;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class PvPManager extends FeatherModule implements IPvPManager {
    private Map<UUID, Long> playersInCombat = null;
    private IConfigFile config = null;
    private TranslationManager lang = null;
    @SuppressWarnings("unused")
    private BukkitTask combatCheckTask = null;

    public PvPManager(String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(IFeatherCoreProvider core) throws FeatherSetupException {
        final var configManager = core.getConfigurationManager();
        this.config = configManager.getPvPConfigFile();
        this.lang = core.getTranslationManager();

        this.playersInCombat = new HashMap<>();

        this.combatCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(core.getPlugin(),
                new CombatChecker(this), 0, this.config.getInt("combat.check-interval") * 20L);
    }

    @Override
    protected void onModuleDisable() {
        if (this.playersInCombat != null) {
            this.playersInCombat.clear();
        }
    }

    @Override
    public boolean isPlayerInCombat(final Player player) {
        return this.playersInCombat.containsKey(player.getUniqueId());
    }

    @Override
    public boolean isPlayerInCombat(final UUID uuid) {
        return this.playersInCombat.containsKey(uuid);
    }

    @Override
    public void putPlayersInCombat(final Player victim, final Player attacker) {
        if (victim.getUniqueId().equals(attacker.getUniqueId())) {
            return;
        }

        final var currentTime = System.currentTimeMillis();
        putPlayerInCombat(attacker, victim.getName(), currentTime, Message.TAG);
        putPlayerInCombat(victim, attacker.getName(), currentTime, Message.TAGGED);
    }

    private void putPlayerInCombat(final Player player, final String otherName,
            final long currentTime, final String messageKey) {
        if (!this.playersInCombat.containsKey(player.getUniqueId())) {
            this.lang.message(player, messageKey,
                    Pair.of(Placeholder.PLAYER, otherName));
            if (!player.hasPermission("pvp.bypass.fly")) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }
        this.playersInCombat.put(player.getUniqueId(), currentTime);
    }

    @Override
    public void removePlayerInCombat(final Player player) {
        this.playersInCombat.remove(player.getUniqueId());
        this.lang.message(player, Message.COMBAT_ENDED);
    }

    @Override
    public void removePlayerInCombat(final UUID uuid) {
        this.playersInCombat.remove(uuid);
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            this.lang.message(player, Message.COMBAT_ENDED);
        }
    }

    @Override
    public List<String> getWhitelistedCommands() {
        return this.config.getStringList("commands.whitelist");
    }

    private Map<UUID, Long> getPlayersInCombat() {
        return this.playersInCombat;
    }

    public long getCombatTimeMillis() {
        return this.config.getInt("combat.time") * 1000;
    }

    private static class CombatChecker implements Runnable {
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
            final var currentTime = System.currentTimeMillis();

            final var combatTime = this.pvpManager.getCombatTimeMillis();
            final var iterator = this.pvpManager.getPlayersInCombat().entrySet().iterator();
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
