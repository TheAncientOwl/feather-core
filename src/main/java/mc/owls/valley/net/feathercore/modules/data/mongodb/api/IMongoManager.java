package mc.owls.valley.net.feathercore.modules.data.mongodb.api;

import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;

public interface IMongoManager {
    public PlayersDAO getPlayersDAO();
}
