/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IDAOAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description MongoDB data accessor interface
 */

package mc.owls.valley.net.feathercore.modules.data.mongodb.api;

import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;

public interface IDAOAccessor {
    public PlayersDAO getPlayersDAO();

    public LootChestsDAO getLootChestsDAO();
}
