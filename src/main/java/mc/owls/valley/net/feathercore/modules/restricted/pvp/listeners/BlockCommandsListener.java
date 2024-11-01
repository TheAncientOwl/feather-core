/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BlockCommandsListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Allow only whitelisted commands during combat
 */

package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Message;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IRestrictedPvP;

public class BlockCommandsListener implements IFeatherListener {
    private IRestrictedPvP pvpManager = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getRestrictedPvP();
        this.lang = core.getLanguageManager();
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
        this.lang.message(player, Message.BLOCK_COMMAND);
    }

}
