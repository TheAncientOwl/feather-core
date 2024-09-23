package mc.owls.valley.net.feathercore.api.database.mongo;

import mc.owls.valley.net.feathercore.api.database.mongo.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.api.database.mongo.accessors.PlayersDAO;

public interface IDAOAccessor {
    public PlayersDAO getPlayersDAO();

    public LootChestsDAO getLootChestsDAO();
}
