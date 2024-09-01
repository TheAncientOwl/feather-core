package mc.owls.valley.net.feathercore.databases.mongodb;

import org.bson.UuidRepresentation;

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
import mc.owls.valley.net.feathercore.databases.mongodb.data.accessors.AbstractDAO;

public class MongoDBHandler {
    private MongoClient mongoClient = null;
    private Datastore datastore = null;

    private boolean connectionOK = false;

    @SuppressWarnings("rawtypes")
    public MongoDBHandler(final String uri, final String databaseName, Class... entities) {
        try {
            final ConnectionString connectionString = new ConnectionString(uri);

            final MongoClientSettings settings = MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                    .build();

            this.mongoClient = MongoClients.create(settings);

            this.datastore = Morphia.createDatastore(
                    this.mongoClient, databaseName, MapperOptions.builder()
                            .dateStorage(DateStorage.SYSTEM_DEFAULT)
                            .build());

            this.datastore.getMapper().map(entities);
            this.datastore.ensureIndexes();

            this.connectionOK = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.connectionOK = false;
        }
    }

    public boolean connected() {
        return this.connectionOK;
    }

    public <DAOType extends AbstractDAO<?>> DAOType getDAO(Class<DAOType> daoClass) {
        try {
            return daoClass.getDeclaredConstructor(Datastore.class).newInstance(this.datastore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DAO instance for " + daoClass.getName(), e);
        }
    }
}
