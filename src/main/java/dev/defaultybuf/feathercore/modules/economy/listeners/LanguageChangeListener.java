/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickListener.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Update banknote meta when player's language changes
 */

package dev.defaultybuf.feathercore.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataType;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.NamespacedKey;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.core.FeatherListener;
import dev.defaultybuf.feathercore.modules.economy.interfaces.IFeatherEconomy;
import dev.defaultybuf.feathercore.modules.language.events.LanguageChangeEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LanguageChangeListener extends FeatherListener {
    public LanguageChangeListener(final InitData data) {
        super(data);
    }

    @EventHandler
    public void onLanguageChange(final LanguageChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final var translation = event.getTranslation();

        final var banknoteLore = translation.getStringList(Message.Economy.BANKNOTE_LORE);
        final var banknoteName = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(translation.getString(Message.Economy.BANKNOTE_NAME));

        for (final var itemStack : event.getPlayer().getInventory()) {
            if (itemStack == null) {
                continue;
            }

            final var meta = itemStack.getItemMeta();

            if (meta == null) {
                continue;
            }

            final var valueKey = new NamespacedKey(getPlugin(), meta,
                    getInterface(IFeatherEconomy.class).getConfig().getString("banknote.key"));
            if (!valueKey.isPresent()) {
                continue;
            }
            final var banknoteValue = valueKey.get(PersistentDataType.DOUBLE);

            meta.displayName(banknoteName);
            meta.lore(banknoteLore.stream()
                    .map(line -> LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(
                                    StringUtils.replacePlaceholders(line,
                                            Pair.of(Placeholder.AMOUNT, banknoteValue))))
                    .toList());

            itemStack.setItemMeta(meta);
        }
    }

}
