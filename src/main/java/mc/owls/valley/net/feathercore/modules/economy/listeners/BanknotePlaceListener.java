package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.IFeatherListener;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.utils.ChatUtils;

public class BanknotePlaceListener implements IFeatherListener {
    private JavaPlugin plugin = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.plugin = plugin;
        this.messages = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItemInHand().getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(this.plugin, Message.BANKNOTE_METADATA_KEY))) {
            event.setCancelled(true);
            ChatUtils.sendMessage(event.getPlayer(), this.messages, Message.BANKNOTE_PLACE);
        }
    }

}
