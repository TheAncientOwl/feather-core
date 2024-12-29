/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestsDAO.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description MongoDB loot-chests data accessor
 */

package dev.defaultybuf.feathercore.modules.data.mongodb.api.accessors;

import java.util.UUID;

import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import dev.morphia.Datastore;

public class LootChestsDAO extends AbstractDAO<LootChestsModel> {

    public LootChestsDAO(Datastore datastore) {
        super(datastore);
    }

    public LootChestsModel getChests() {
        LootChestsModel model = null;

        try {
            model = findFirst("version", LootChestsModel.VERSION);
        } catch (final IndexOutOfBoundsException e) {
            model = new LootChestsModel();
            model.uuid = UUID.randomUUID();
            save(model);
        }

        return model;
    }

    @Override
    protected Class<LootChestsModel> getEntityClass() {
        return LootChestsModel.class;
    }

}
