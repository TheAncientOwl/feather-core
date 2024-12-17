/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IMongoDB.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description MongoDB module interface
 */

package mc.owls.valley.net.feathercore.modules.data.mongodb.interfaces;

import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;

public interface IMongoDB {
    public PlayersDAO getPlayersDAO();

    public LootChestsDAO getLootChestsDAO();
}
