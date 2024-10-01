/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherListener.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Listener interface for paper events
 */

package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.event.Listener;

import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;

public interface IFeatherListener extends Listener {
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException;
}
