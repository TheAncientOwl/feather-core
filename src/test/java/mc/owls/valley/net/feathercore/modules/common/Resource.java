/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Resource.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class to resource related data
 */
package mc.owls.valley.net.feathercore.modules.common;

import java.nio.file.Path;

public class Resource {
    private final Path path;
    private final String content;

    public Resource(Path path, String content) {
        this.path = path;
        this.content = content;
    }

    public Resource(String path, String content) {
        this(Path.of(path), content);
    }

    public Path path() {
        return path;
    }

    public String content() {
        return content;
    }

    public static Resource of(Path path, String content) {
        return new Resource(path, content);
    }

    public static Resource of(String path, String content) {
        return new Resource(path, content);
    }
}
