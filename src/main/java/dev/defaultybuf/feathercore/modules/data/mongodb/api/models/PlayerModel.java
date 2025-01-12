/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerModel.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Player data model
 */

package dev.defaultybuf.feathercore.modules.data.mongodb.api.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "players_data", useDiscriminator = false)
public class PlayerModel {
    @Id public UUID uuid;

    @Property("username") public String name;

    @Property("nickname") public String nickname;

    @Property("registration_date") public Date registrationDate;

    @Property("last_login") public Date lastLogin;

    @Property("balance") public double balance;

    @Property("accepts_payments") public boolean acceptsPayments = true;

    @Property("language") public String language = "en";

    @Property("loot_chests_open_times") public Map<String, Long> chestLocationToOpenTime =
            new HashMap<>();

    @Property("last_known_location") public LocationModel lastKnownLocation;

    public PlayerModel() {}

    public PlayerModel(final Player player, final double balance, final String language) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.nickname = "";
        this.registrationDate = new Date();
        this.lastLogin = new Date();
        this.balance = balance;
        this.acceptsPayments = true;
        this.language = language;
        this.lastKnownLocation = new LocationModel(player.getLocation());
    }

    public void compatibilityUpdate(final Player player) {
        if (this.lastLogin == null) {
            this.lastLogin = new Date();
        }

        if (this.language == null) {
            this.language = "en";
        }

        if (this.chestLocationToOpenTime == null) {
            this.chestLocationToOpenTime = new HashMap<>();
        }

        if (this.lastKnownLocation == null) {
            this.lastKnownLocation = new LocationModel(player.getLocation());
        }
    }
}
