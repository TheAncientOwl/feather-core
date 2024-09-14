package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;

public class BanknotePlaceListener implements IFeatherListener {
    private JavaPlugin plugin = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.plugin = core.getPlugin();
        this.messages = core.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItemInHand().getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(this.plugin, Messages.BANKNOTE_METADATA_KEY))) {
            event.setCancelled(true);
            Message.to(event.getPlayer(), this.messages, Messages.BANKNOTE_PLACE);
        }
    }

}
