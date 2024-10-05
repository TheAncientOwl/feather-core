/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file NamespacedKey.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility to manage inventory ItemMeta namespaced key
 */

package mc.owls.valley.net.feathercore.api.common.minecraft;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class NamespacedKey {

    private final org.bukkit.NamespacedKey key;
    private final PersistentDataContainer container;

    public NamespacedKey(final JavaPlugin plugin, final ItemMeta itemMeta, final String key) {
        this.key = new org.bukkit.NamespacedKey(plugin, key);
        this.container = itemMeta.getPersistentDataContainer();
    }

    public boolean isPresent() {
        return this.container == null || this.container.has(this.key);
    }

    public <P, C> C get(PersistentDataType<P, C> type) {
        return this.container.get(this.key, type);
    }

    public <P, C> void set(final PersistentDataType<P, C> type, final C value) {
        this.container.set(this.key, type, value);
    }

}
