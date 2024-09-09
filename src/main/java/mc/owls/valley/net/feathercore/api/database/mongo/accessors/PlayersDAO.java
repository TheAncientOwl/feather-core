package mc.owls.valley.net.feathercore.api.database.mongo.accessors;

import dev.morphia.Datastore;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;

public class PlayersDAO extends AbstractDAO<PlayerModel> {

    public PlayersDAO(Datastore datastore) {
        super(datastore);
    }

    @Deprecated
    public PlayerModel getModelByName(final String playerName) {
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
