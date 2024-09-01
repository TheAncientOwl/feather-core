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

public class MongoDBHandler {
    private MongoClient mongoClient;
    private Datastore datastore;

    private boolean connectionOK;

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
                            // .discriminatorKey("")
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

    public Datastore getDatastore() {
        return this.datastore;
    }
}
