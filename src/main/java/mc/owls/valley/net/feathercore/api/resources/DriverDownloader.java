package mc.owls.valley.net.feathercore.api.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.log.FeatherLogger;

public class DriverDownloader {
    public static class Config {
        public final String tag;
        public final String driverURL;
        public final String jarName;

        public Config(final String tag, final String driverURL, final String jarName) {
            this.tag = tag;
            this.driverURL = driverURL;
            this.jarName = jarName;
        }
    }

    final Config config;
    final JavaPlugin plugin;
    final FeatherLogger logger;

    public DriverDownloader(final JavaPlugin plugin, final FeatherLogger logger, final Config config) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
    }

    public boolean setupJAR() {
        final File libDir = new File(this.plugin.getDataFolder(), "lib");
        if (!libDir.exists()) {
            libDir.mkdirs();
        }

        final File mongoJar = new File(libDir, this.config.jarName);
        if (!mongoJar.exists()) {
            this.logger.info(this.config.tag + "Driver not found. Downloading...");

            if (!downloadFile(this.config.driverURL, mongoJar)) {
                return false;
            }
        } else {
            this.logger.success(this.config.tag + "Driver found!");
        }

        try {
            addFileToClasspath(mongoJar);
        } catch (Exception e) {
            this.logger.error(this.config.tag + "Failed to add driver to class path.");
            this.logger.error(this.config.tag + "JAR setup failed.");
            e.printStackTrace();
            return false;
        }

        this.logger.success(this.config.tag + "JAR setup finished successfully!");

        return true;
    }

    private boolean downloadFile(String fileUrl, File destination) {
        try {
            final URL url = new URI(fileUrl).toURL();

            try (final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    final FileOutputStream fos = new FileOutputStream(destination)) {
                final ByteBuffer buffer = ByteBuffer.allocate(8192);
                while (rbc.read(buffer) > 0) {
                    buffer.flip();
                    fos.getChannel().write(buffer);
                    buffer.clear();
                }
                this.logger.success(this.config.tag + "Driver downloaded successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error(this.config.tag + "Failed to download driver.");
            return false;
        }

        return true;
    }

    private class ClassLoaderHelper extends URLClassLoader {
        public ClassLoaderHelper(URL[] urls) {
            super(urls);
        }

        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    private void addFileToClasspath(File file) throws Exception {
        final ClassLoaderHelper customClassLoader = new ClassLoaderHelper(new URL[] {});
        customClassLoader.addURL(file.toURI().toURL());
        customClassLoader.close();
    }
}
