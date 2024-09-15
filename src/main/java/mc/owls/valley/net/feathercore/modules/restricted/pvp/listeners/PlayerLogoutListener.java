package mc.owls.valley.net.feathercore.modules.restricted.pvp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPvPManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.restricted.pvp.common.Messages;

public class PlayerLogoutListener implements IFeatherListener {
    private IPvPManager pvpManager = null;
    private ITranslationAccessor lang = null;
    private IPlayersDataManager playersData = null;
    private IPropertyAccessor config = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        try {
            this.pvpManager = core.getPvPManager();

            this.config = core.getConfigurationManager().getPvPConfigFile();

            this.lang = core.getTranslationManager();
            this.playersData = core.getPlayersDataManager();
        } catch (final ModuleNotEnabledException e) {
        }
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
        Message.broadcast(this.lang.getTranslation("en"), Messages.LOGOUT,
                Pair.of(Placeholder.PLAYER, player.getName()));

    }
}
