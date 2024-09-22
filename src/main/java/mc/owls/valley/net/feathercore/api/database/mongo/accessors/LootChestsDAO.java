package mc.owls.valley.net.feathercore.api.database.mongo.accessors;

import java.util.UUID;

import dev.morphia.Datastore;
import mc.owls.valley.net.feathercore.api.database.mongo.models.LootChestsModel;

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
