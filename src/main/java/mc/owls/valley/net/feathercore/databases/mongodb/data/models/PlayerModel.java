package mc.owls.valley.net.feathercore.databases.mongodb.data.models;

import java.util.Date;
import java.util.UUID;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "players_data", useDiscriminator = false)
public class PlayerModel {
    @Id
    public UUID uuid;

    @Property("username")
    public String username;

    @Property("nickname")
    public String nickname;

    @Property("registration_date")
    public Date registrationDate;

    @Property("last_login")
    public Date lastLogin;

    @Property("balance")
    public double balance;

    public PlayerModel() {
    }
}
