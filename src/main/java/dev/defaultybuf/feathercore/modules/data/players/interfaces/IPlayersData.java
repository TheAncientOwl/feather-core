/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IPlayersData.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description PlayersData module interface
 */

package dev.defaultybuf.feathercore.modules.data.players.interfaces;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;

public interface IPlayersData {
    public void handleNewPlayer(final Player player);

    public void savePlayersData();

    /**
     * @deprecated As of FeatherCore 0.2.3 use {{@link #getPlayerModel(UUID)}} or
     *             {{@link #getPlayerModel(Player)}} instead
     */
    @Deprecated
    public PlayerModel getPlayerModel(final String name);

    public PlayerModel getPlayerModel(final UUID uuid);

    public PlayerModel getPlayerModel(final Player player);

    public PlayerModel getPlayerModel(final OfflinePlayer player);

    public void savePlayerModel(final PlayerModel playerModel);

    /**
     * @deprecated As of FeatherCore 0.2.3 use {{@link #markPlayerModelForSave(UUID)}} or
     *             {{@link #markPlayerModelForSave(Player)}} instead
     */
    public boolean markPlayerModelForSave(final String name);

    public boolean markPlayerModelForSave(final UUID uuid);

    public boolean markPlayerModelForSave(final Player player);

    public boolean markPlayerModelForSave(final OfflinePlayer player);

    public boolean markPlayerModelForSave(final PlayerModel playerModel);
}
