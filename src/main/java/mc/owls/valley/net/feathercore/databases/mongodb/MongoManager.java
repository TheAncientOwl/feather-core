package mc.owls.valley.net.feathercore.databases.mongodb;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.FeatherCore;
import mc.owls.valley.net.feathercore.api.resources.DriverDownloader;
import mc.owls.valley.net.feathercore.log.FeatherLogger;

public class MongoManager extends DriverDownloader {
    private static final String TAG = "MongoManager: ";
    private static final String MONGO_DRIVER_URL = "https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/5.1.0/mongodb-driver-sync-5.1.0.jar";
    private static final String MONGO_JAR_NAME = "mongodb-driver-sync-5.1.0.jar";

    final JavaPlugin plugin;
    final FeatherLogger logger;

    public MongoManager(final FeatherCore plugin) {
        super(plugin, plugin.getFeatherLogger(), new Config(TAG, MONGO_DRIVER_URL, MONGO_JAR_NAME));
        this.plugin = plugin;
        this.logger = plugin.getFeatherLogger();
    }

    public boolean connect() {
        return false;
    }
}
