/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModule.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Base class for plugin module
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.function.Supplier;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public abstract class FeatherModule {
    public final static String HIDE_LIFECYCLE_PREFIX = "$";

    private final String name;
    protected final IConfigFile config;

    public FeatherModule(final String name, final Supplier<IConfigFile> configSupplier) {
        this.name = name;
        this.config = configSupplier.get();
    }

    public void onEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final IFeatherLogger logger = core.getFeatherLogger();

        logStatus(logger, "&7setup started");
        onModuleEnable(core);
        logStatus(logger, "&aenabled");
    }

    public void onDisable(final IFeatherLogger logger) {
        logStatus(logger, "&7disabling started");
        onModuleDisable();
        logStatus(logger, "&adisabled");
    }

    public String getModuleName() {
        return this.name;
    }

    public IConfigFile getConfig() {
        return this.config;
    }

    private void logStatus(final IFeatherLogger logger, final String message) {
        if (!name.startsWith(HIDE_LIFECYCLE_PREFIX)) {
            logger.info("&2" + this.name + "&8: &r" + message);
        }

    }

    protected abstract void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException;

    protected abstract void onModuleDisable();

}
