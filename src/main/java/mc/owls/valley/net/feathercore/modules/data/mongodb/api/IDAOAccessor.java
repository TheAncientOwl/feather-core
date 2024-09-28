package mc.owls.valley.net.feathercore.modules.data.mongodb.api;

import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;

public interface IDAOAccessor {
    public PlayersDAO getPlayersDAO();

    public LootChestsDAO getLootChestsDAO();
}
