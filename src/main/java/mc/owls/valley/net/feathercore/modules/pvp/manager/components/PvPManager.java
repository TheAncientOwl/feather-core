/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PvPManager.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Module responsible for managing pvp restrictions
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.pvp.manager.common.Message;
import mc.owls.valley.net.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class PvPManager extends FeatherModule implements IPvPManager {
    private Map<UUID, Long> playersInCombat = null;
    private LanguageManager lang = null;
    @SuppressWarnings("unused")
    private BukkitTask combatCheckTask = null;

    public PvPManager(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.lang = core.getLanguageManager();

        this.playersInCombat = new HashMap<>();

        this.combatCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(core.getPlugin(),
                new CombatChecker(this), 0, this.config.getTicks("combat.check-interval"));
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
        return this.config.getMillis("combat.time");
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
