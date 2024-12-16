/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherListener.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Listener interface for paper events
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.Map;

import org.bukkit.event.Listener;

public abstract class FeatherListener extends ModulesAccessor implements Listener {
    public static final record InitData(Map<Class<?>, Object> modules) {
    }

    public FeatherListener(final InitData data) {
        super(data.modules);
    }
}
