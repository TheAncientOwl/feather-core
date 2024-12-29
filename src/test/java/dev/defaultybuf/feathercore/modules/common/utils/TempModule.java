/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file TempModule.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Helper class to delete given module files when no longer needed
 */
package dev.defaultybuf.feathercore.modules.common.utils;

import java.util.List;

import dev.defaultybuf.feathercore.api.core.FeatherModule;

public class TempModule<T extends FeatherModule> implements AutoCloseable {
    private final List<TempFile> resources;
    private final T module;

    public TempModule(T module, List<TempFile> resources) {
        this.module = module;
        this.resources = resources;
    }

    @Override
    public void close() {
        for (var resource : resources) {
            resource.close();
        }
    }

    public T module() {
        return module;
    }
}
