/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BlockCommandsListener.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Allow only whitelisted commands during combat
 */

package mc.owls.valley.net.feathercore.modules.pvp.manager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.core.FeatherListener;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;
import mc.owls.valley.net.feathercore.modules.pvp.manager.interfaces.IPvPManager;

public class BlockCommandsListener extends FeatherListener {
    public BlockCommandsListener(final InitData data) {
        super(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (event.isCancelled() || !getInterface(IPvPManager.class).isPlayerInCombat(player)
                || player.hasPermission("pvp.bypass.commands")) {
            return;
        }

        final String command = event.getMessage().toLowerCase();
        for (final var allowedCommand : getInterface(IPvPManager.class).getWhitelistedCommands()) {
            if (command.startsWith(allowedCommand)) {
                return;
            }
        }

        event.setCancelled(true);
        getInterface(ILanguage.class).message(player, Message.PvPManager.BLOCK_COMMAND);
    }

}
