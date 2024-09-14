package mc.owls.valley.net.feathercore.modules.restricted.pvp.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Messages;

public class PvPManager extends FeatherModule implements IPvPManager {
    private Map<UUID, Long> playersInCombat = null;
    private IConfigFile config = null;
    private IPropertyAccessor messages = null;
    @SuppressWarnings("unused")
    private BukkitTask combatCheckTask = null;

    public PvPManager(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(IFeatherCoreProvider core) throws FeatherSetupException {
        final IConfigurationManager configManager = core.getConfigurationManager();
        this.config = configManager.getPvPConfigFile();
        this.messages = configManager.getMessagesConfigFile().getConfigurationSection(Messages.PVP_SECTION);

        this.playersInCombat = new HashMap<>();

        this.combatCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(core.getPlugin(),
                new CombatChecker(this), 0, this.config.getInt("combat.check-interval") * 20L);

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
        this.playersInCombat.clear();
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
        putPlayerInCombat(attacker, victim.getName(), currentTime, Messages.TAG);
        putPlayerInCombat(victim, attacker.getName(), currentTime, Messages.TAGGED);
    }

    private void putPlayerInCombat(final Player player, final String otherName,
            final long currentTime, final String messageKey) {
        if (!this.playersInCombat.containsKey(player.getUniqueId())) {
            Message.to(player, this.messages, messageKey, Pair.of(Placeholder.PLAYER_NAME, otherName));
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
        Message.to(player, this.messages, Messages.COMBAT_ENDED);
    }

    @Override
    public void removePlayerInCombat(final UUID uuid) {
        this.playersInCombat.remove(uuid);
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            Message.to(player, this.messages, Messages.COMBAT_ENDED);
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
