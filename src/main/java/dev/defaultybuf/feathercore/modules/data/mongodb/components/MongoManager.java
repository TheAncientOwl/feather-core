/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file MongoManager.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Module responsible for MongoDB connection
 */

package dev.defaultybuf.feathercore.modules.data.mongodb.components;

import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.accessors.LootChestsDAO;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.accessors.PlayersDAO;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LocationModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.LootChestsModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.mongodb.interfaces.IMongoDB;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.MapperOptions;

public class MongoManager extends FeatherModule implements IMongoDB {
    private MongoClient mongoClient = null;
    private Datastore datastore = null;

    public MongoManager(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        final ConnectionString connectionString =
                new ConnectionString(this.config.getString("uri"));

        final MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout((int) this.config.getMillis("timeouts.connection"),
                            TimeUnit.MILLISECONDS);
                })
                .applyToClusterSettings(builder -> {
                    builder.serverSelectionTimeout(this.config.getMillis("timeouts.selection"),
                            TimeUnit.MILLISECONDS);
                })
                .build();

        this.mongoClient = MongoClients.create(settings);

        try {
            this.mongoClient.listDatabaseNames().first();
        } catch (final MongoTimeoutException e) {
            throw new FeatherSetupException(
                    "Could not connect to mongodb at " + this.config.getString("uri"));
        }

        this.datastore = Morphia.createDatastore(
                this.mongoClient, this.config.getString("dbname"), MapperOptions.builder()
                        .dateStorage(DateStorage.SYSTEM_DEFAULT)
                        .build());

        final var mapper = this.datastore.getMapper();
        mapper.map(PlayerModel.class);
        mapper.map(LootChestsModel.class);
        mapper.map(LocationModel.class);
        this.datastore.ensureIndexes();
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

    @Override
    public LootChestsDAO getLootChestsDAO() {
        return new LootChestsDAO(this.datastore);
    }

}
