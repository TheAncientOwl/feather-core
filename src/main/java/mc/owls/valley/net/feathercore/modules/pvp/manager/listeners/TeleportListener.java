/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TeleportListener.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Block combat teleport
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.pvp.manager.components.PvPManager;

public class TeleportListener implements IFeatherListener {
    private PvPManager pvpManager = null;
    private IConfigFile config = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getPvPManager();
        this.config = core.getPvPManager().getConfig();
        this.lang = core.getLanguageManager();
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
            this.lang.message(player, Message.PvPManager.TELEPORT);
        }
    }
}
