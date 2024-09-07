package mc.owls.valley.net.feathercore.api.data.mongo;

import mc.owls.valley.net.feathercore.api.data.mongo.accessors.PlayersDAO;

public interface IMongoDB {
    public PlayersDAO getPlayersDAO();
}
