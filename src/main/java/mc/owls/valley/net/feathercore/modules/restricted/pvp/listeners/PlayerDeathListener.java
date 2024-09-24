package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;

public class PlayerDeathListener implements IFeatherListener {
    private IPvPManager pvpManager = null;

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
