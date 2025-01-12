/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherListener.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Listener interface for paper events
 */

package dev.defaultybuf.feathercore.api.core;

import java.util.Map;

import org.bukkit.event.Listener;

public abstract class FeatherListener extends DependencyAccessor implements Listener {
    public static final record InitData(Map<Class<?>, Object> modules) {
    }

    public FeatherListener(final InitData data) {
        super(data.modules);
    }
}
