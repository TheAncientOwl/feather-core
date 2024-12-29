/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TempFile.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Helper class to delete given file when no longer needed
 */
package dev.defaultybuf.feathercore.modules.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

public class TempFile implements AutoCloseable {
    private final Path path;

    public TempFile(final Path path) {
        this.path = path;
    }

    @Override
    public void close() {
        try {
            FileUtils.delete(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File get() {
        return path.toFile();
    }

    public Path getPath() {
        return path;
    }
}
