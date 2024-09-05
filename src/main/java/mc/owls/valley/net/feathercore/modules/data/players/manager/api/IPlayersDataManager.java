package mc.owls.valley.net.feathercore.modules.data.players.manager.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;

public interface IPlayersDataManager {
    public void handleNewPlayer(final Player player);

    public void savePlayersData();

    public PlayerModel getPlayerModel(final UUID uuid);

    public PlayerModel getPlayerModel(final Player player);

    public void savePlayerModel(final PlayerModel playerModel);

    public boolean markPlayerModelForSave(final UUID uuid);

    public boolean markPlayerModelForSave(final Player player);
}
