package mc.owls.valley.net.feathercore.databases.mongodb.data.accessors;

import org.jetbrains.annotations.NotNull;

import dev.morphia.Datastore;
import mc.owls.valley.net.feathercore.databases.mongodb.data.models.PlayerData;

public class PlayerDataDAO extends AbstractDAO<PlayerData> {

    public PlayerDataDAO(@NotNull Datastore datastore) {
        super(datastore);
    }

    @Override
    protected Class<PlayerData> getEntityClass() {
        return PlayerData.class;
    }
}
