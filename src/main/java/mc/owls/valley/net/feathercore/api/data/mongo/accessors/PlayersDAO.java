package mc.owls.valley.net.feathercore.api.data.mongo.accessors;

import org.jetbrains.annotations.NotNull;

import dev.morphia.Datastore;
import mc.owls.valley.net.feathercore.api.data.mongo.models.PlayerModel;

public class PlayersDAO extends AbstractDAO<PlayerModel> {

    public PlayersDAO(@NotNull Datastore datastore) {
        super(datastore);
    }

    @Deprecated
    public PlayerModel getModelByName(@NotNull final String playerName) {
        PlayerModel playerModel = null;
        try {
            playerModel = findFirst("username", playerName);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return playerModel;
    }

    @Override
    protected Class<PlayerModel> getEntityClass() {
        return PlayerModel.class;
    }
}
