package mc.owls.valley.net.feathercore.api.database.mongo.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "players_data", useDiscriminator = false)
public class PlayerModel {
    @Id
    public UUID uuid;

    @Property("username")
    public String name;

    @Property("nickname")
    public String nickname;

    @Property("registration_date")
    public Date registrationDate;

    @Property("last_login")
    public Date lastLogin;

    @Property("balance")
    public double balance;

    @Property("accepts_payments")
    public boolean acceptsPayments = true;

    @Property("language")
    public String language = "en";

    @Property("loot_chests_open_times")
    public Map<String, Long> chestLocationToOpenTime = new HashMap<>();

    public PlayerModel() {
    }
}
