package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigFile;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Message;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class TeleportListener implements IFeatherListener {
    private IPvPManager pvpManager = null;
    private IConfigFile config = null;
    private TranslationManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getPvPManager();
        this.config = core.getConfigurationManager().getPvPConfigFile();
        this.lang = core.getTranslationManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (event.isCancelled() || !this.pvpManager.isPlayerInCombat(player)
                || player.hasPermission("pvp.bypass.teleport")) {
            return;
        }

        if ((event.getCause() == TeleportCause.CHORUS_FRUIT && !this.config.getBoolean("block-tp.chorus-fruit")) ||
                (event.getCause() == TeleportCause.ENDER_PEARL && !this.config.getBoolean("block-tp.ender-pearl"))) {
            event.setCancelled(true);
            this.lang.message(player, Message.TELEPORT);
        }
    }
}
