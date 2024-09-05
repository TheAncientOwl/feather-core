package mc.owls.valley.net.feathercore.modules.data.mongodb;

import org.bson.UuidRepresentation;
import org.bukkit.configuration.ConfigurationSection;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.MapperOptions;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.IMongoManager;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;

public class MongoManager extends FeatherModule implements IMongoManager {
    private MongoClient mongoClient = null;
    private Datastore datastore = null;

    public MongoManager(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) {
        final ConfigurationSection mongoConfig = plugin.getConfig().getConfigurationSection("mongodb");

        final ConnectionString connectionString = new ConnectionString(mongoConfig.getString("uri"));

        final MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();

        this.mongoClient = MongoClients.create(settings);

        this.datastore = Morphia.createDatastore(
                this.mongoClient, mongoConfig.getString("dbname"), MapperOptions.builder()
                        .dateStorage(DateStorage.SYSTEM_DEFAULT)
                        .build());

        this.datastore.getMapper().map(PlayerModel.class);
        this.datastore.ensureIndexes();

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    public void onModuleDisable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    @Override
    public PlayersDAO getPlayersDAO() {
        return new PlayersDAO(this.datastore);
    }
}
