package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Messages;

public class TeleportListener implements IFeatherListener {
    private IPvPManager pvpManager = null;
    private IConfigFile config = null;
    private ITranslationAccessor lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        try {
            this.pvpManager = core.getPvPManager();
            this.config = core.getConfigurationManager().getPvPConfigFile();
            this.lang = core.getTranslationManager();
        } catch (final ModuleNotEnabledException e) {
        }
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
            Message.to(player, this.lang.getTranslation(player), Messages.TELEPORT);
        }
    }
}
