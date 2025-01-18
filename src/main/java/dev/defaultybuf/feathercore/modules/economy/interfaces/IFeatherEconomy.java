/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherEconomy.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Economy module interface
 */

package dev.defaultybuf.feathercore.modules.economy.interfaces;

import dev.defaultybuf.feather.toolkit.api.interfaces.IConfigAccessor;
import net.milkbowl.vault.economy.Economy;

public interface IFeatherEconomy extends IConfigAccessor {
    public Economy getEconomy();
}
