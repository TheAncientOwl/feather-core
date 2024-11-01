/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickListener.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Change banknote meta when picked up based on language
 */

package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class BanknotePickupListener implements IFeatherListener {
    private JavaPlugin plugin = null;
    private IConfigFile economyConfig = null;
    private LanguageManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException {
        this.plugin = core.getPlugin();
        this.economyConfig = core.getFeatherEconomy().getConfig();
        this.lang = core.getLanguageManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPickup(final EntityPickupItemEvent event) {
        if (event.isCancelled() || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        final var itemStack = event.getItem().getItemStack();
        final var meta = itemStack.getItemMeta();

        if (meta == null) {
            return;
        }

        final var valueKey = new NamespacedKey(this.plugin, meta,
                this.economyConfig.getString("banknote.key"));
        if (!valueKey.isPresent()) {
            return;
        }
        final var banknoteValue = valueKey.get(PersistentDataType.DOUBLE);

        final var sender = (Player) event.getEntity();
        final var lore = this.lang.getTranslation(sender).getStringList(Message.BANKNOTE_LORE);

        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(this.lang.getTranslation(sender).getString(Message.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line, Pair.of(Placeholder.AMOUNT, banknoteValue))))
                .toList());

        itemStack.setItemMeta(meta);
    }

}
