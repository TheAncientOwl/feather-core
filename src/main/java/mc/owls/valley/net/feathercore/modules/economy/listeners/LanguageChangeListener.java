/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file BanknotePickListener.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Update banknote meta when player's language changes
 */

package mc.owls.valley.net.feathercore.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.NamespacedKey;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherListener;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.modules.language.events.LanguageChangeEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LanguageChangeListener implements IFeatherListener {
    private JavaPlugin plugin = null;
    private IConfigFile economyConfig = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException {
        this.plugin = core.getPlugin();
        this.economyConfig = core.getFeatherEconomy().getConfig();
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

            final var valueKey = new NamespacedKey(this.plugin, meta, this.economyConfig.getString("banknote.key"));
            if (!valueKey.isPresent()) {
                continue;
            }
            final var banknoteValue = valueKey.get(PersistentDataType.DOUBLE);

            meta.displayName(banknoteName);
            meta.lore(banknoteLore.stream()
                    .map(line -> LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(
                                    StringUtils.replacePlaceholders(line, Pair.of(Placeholder.AMOUNT, banknoteValue))))
                    .toList());

            itemStack.setItemMeta(meta);
        }
    }

}
