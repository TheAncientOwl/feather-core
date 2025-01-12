/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayersDAO.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description MongoDB players-data accessor
 */

package dev.defaultybuf.feathercore.modules.data.mongodb.api.accessors;

import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.morphia.Datastore;

public class PlayersDAO extends AbstractDAO<PlayerModel> {

    public PlayersDAO(Datastore datastore) {
        super(datastore);
    }

    public PlayerModel getModelByName(final String playerName) {
        PlayerModel playerModel = null;
        try {
            playerModel = findFirst("username", playerName);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
        return playerModel;
    }

    @Override
    protected Class<PlayerModel> getEntityClass() {
        return PlayerModel.class;
    }
}
