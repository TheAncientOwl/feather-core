/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerLogoutListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Kill player in combat on disconnect
 */

package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.owls.valley.net.feathercore.api.common.Broadcast;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Message;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.interfaces.IRestrictedPvP;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class PlayerLogoutListener implements IFeatherListener {
    private IRestrictedPvP pvpManager = null;
    private TranslationManager lang = null;
    private IPropertyAccessor config = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.pvpManager = core.getRestrictedPvP();
        this.config = core.getRestrictedPvP().getConfig();
        this.lang = core.getTranslationManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!this.pvpManager.isPlayerInCombat(player) || !this.config.getBoolean("on-logout.kill")
                || player.hasPermission("pvp.bypass.killonlogout")) {
            return;
        }

        this.pvpManager.removePlayerInCombat(player);
        player.setHealth(0);

        if (!this.config.getBoolean("on-logout.broadcast")) {
            return;
        }

        // TODO: Broadcast for each player in prefered language
        Broadcast.broadcast(this.lang.getTranslation("en"), Message.LOGOUT,
                Pair.of(Placeholder.PLAYER, player.getName()));
    }
}
