/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModule.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Base class for plugin module
 */

package mc.owls.valley.net.feathercore.api.core;

import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public abstract class FeatherModule {
    private final static String HIDE_LIFECYCLE_PREFIX = "$";
    private final String name;

    public FeatherModule(final String name) {
        this.name = name;
    }

    public void onEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final IFeatherLogger logger = core.getFeatherLogger();

        if (logger.isInitialized() && !name.startsWith(HIDE_LIFECYCLE_PREFIX)) {
            logStatus(logger, "&7setup started");
        }

        onModuleEnable(core);

        if (logger.isInitialized() && !name.startsWith(HIDE_LIFECYCLE_PREFIX)) {
            logStatus(logger, "&aenabled");
        }
    }

    public void onDisable(final IFeatherLogger logger) {
        if (!name.startsWith(HIDE_LIFECYCLE_PREFIX)) {
            logStatus(logger, "&7disabling started");
        }

        onModuleDisable();

        if (!name.startsWith(HIDE_LIFECYCLE_PREFIX)) {
            logStatus(logger, "&adisabled");
        }
    }

    public String getModuleName() {
        return this.name;
    }

    private void logStatus(final IFeatherLogger logger, final String message) {
        logger.info("&2" + this.name + "&8: &r" + message);

    }

    protected abstract void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException;

    protected abstract void onModuleDisable();

}
