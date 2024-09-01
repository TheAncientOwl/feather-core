package mc.owls.valley.net.feathercore.databases.mongodb.data.models;

import java.util.UUID;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value = "players_data", useDiscriminator = false)
public class PlayerData {
    @Id
    public UUID uuid;

    public String username;
    public double balance;

    public PlayerData() {
    }
}
