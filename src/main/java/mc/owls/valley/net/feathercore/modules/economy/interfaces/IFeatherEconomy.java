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

package mc.owls.valley.net.feathercore.modules.economy.interfaces;

import mc.owls.valley.net.feathercore.api.core.IConfigAccessor;
import net.milkbowl.vault.economy.Economy;

public interface IFeatherEconomy extends IConfigAccessor {
    public Economy getEconomy();
}
