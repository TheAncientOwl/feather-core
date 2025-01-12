/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickListener.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Change banknote meta when picked up based on language
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.NamespacedKey;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class BanknotePickupListener extends FeatherListener {
    public BanknotePickupListener(final InitData data) {
        super(data);
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

        final var valueKey = new NamespacedKey(getPlugin(), meta,
                getInterface(IFeatherEconomy.class).getConfig().getString("banknote.key"));
        if (!valueKey.isPresent()) {
            return;
        }
        final var banknoteValue = valueKey.get(PersistentDataType.DOUBLE);

        final var sender = (Player) event.getEntity();
        final var lore = getLanguage().getTranslation(sender)
                .getStringList(Message.Economy.BANKNOTE_LORE);

        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(
                        getLanguage().getTranslation(sender)
                                .getString(Message.Economy.BANKNOTE_NAME)));
        meta.lore(lore.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(StringUtils.replacePlaceholders(line,
                                Pair.of(Placeholder.AMOUNT, banknoteValue))))
                .toList());

        itemStack.setItemMeta(meta);
    }
}
