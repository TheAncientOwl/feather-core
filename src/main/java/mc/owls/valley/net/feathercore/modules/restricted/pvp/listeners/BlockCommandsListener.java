package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Messages;

public class BlockCommandsListener implements IFeatherListener {
    private IPvPManager pvpManager = null;
    private ITranslationAccessor lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        try {
            this.pvpManager = core.getPvPManager();
            this.lang = core.getTranslationManager();
        } catch (final ModuleNotEnabledException e) {
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (event.isCancelled() || !this.pvpManager.isPlayerInCombat(player)
                || player.hasPermission("pvp.bypass.commands")) {
            return;
        }

        final String command = event.getMessage().toLowerCase();
        for (final var allowedCommand : this.pvpManager.getWhitelistedCommands()) {
            if (command.startsWith(allowedCommand)) {
                return;
            }
        }

        event.setCancelled(true);
        Message.to(player, this.lang.getTranslation(player), Messages.BLOCK_COMMAND);
    }

}
