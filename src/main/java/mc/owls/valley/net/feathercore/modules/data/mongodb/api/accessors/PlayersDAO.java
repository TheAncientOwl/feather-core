package mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors;

import org.jetbrains.annotations.NotNull;

import dev.morphia.Datastore;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;

public class PlayersDAO extends AbstractDAO<PlayerModel> {

    public PlayersDAO(@NotNull Datastore datastore) {
        super(datastore);
    }

    @Override
    protected Class<PlayerModel> getEntityClass() {
        return PlayerModel.class;
    }
}
