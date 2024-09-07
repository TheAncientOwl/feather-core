package mc.owls.valley.net.feathercore.modules.data.mongodb;

import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.MapperOptions;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.api.data.mongo.IMongoDB;
import mc.owls.valley.net.feathercore.api.data.mongo.accessors.PlayersDAO;
import mc.owls.valley.net.feathercore.api.data.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.FeatherModule;
import mc.owls.valley.net.feathercore.api.module.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class MongoModule extends FeatherModule implements IMongoDB {
    private MongoClient mongoClient = null;
    private Datastore datastore = null;

    public MongoModule(final String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException {
        final IConfigFile dataConfig = plugin.getConfigurationManager().getDataConfiguration();
        final IConfigSection mongoConfig = dataConfig.getConfigurationSection("mongodb");

        final ConnectionString connectionString = new ConnectionString(mongoConfig.getString("uri"));

        final MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(mongoConfig.getInt("connection.timeout"), TimeUnit.MILLISECONDS);
                })
                .applyToClusterSettings(builder -> {
                    builder.serverSelectionTimeout(mongoConfig.getInt("connection.timeout"), TimeUnit.MILLISECONDS);
                })
                .build();

        this.mongoClient = MongoClients.create(settings);

        try {
            this.mongoClient.listDatabaseNames().first();
        } catch (final MongoTimeoutException e) {
            throw new FeatherSetupException("Could not connect to mongodb at " + mongoConfig.getString("uri"));
        }

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
