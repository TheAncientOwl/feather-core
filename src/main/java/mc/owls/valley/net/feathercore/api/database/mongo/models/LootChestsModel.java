package mc.owls.valley.net.feathercore.api.database.mongo.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "loot_chests", useDiscriminator = false)
public class LootChestsModel {
    public static final String VERSION = "1.0";

    @Id
    public UUID uuid;

    @Property("version")
    public String version = VERSION;

    @Property("chests")
    public Map<String, String> locationToType = new HashMap<>();

    public LootChestsModel() {
    }
}
