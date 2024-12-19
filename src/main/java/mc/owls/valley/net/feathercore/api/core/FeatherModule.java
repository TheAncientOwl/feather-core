/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherModule.java
 * @author Alexandru Delegeanu
 * @version 0.6
 * @description Base class for plugin module
 */

package mc.owls.valley.net.feathercore.api.core;

import java.util.Map;
import java.util.function.Supplier;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public abstract class FeatherModule extends DependencyAccessor {
    public final static String HIDE_LIFECYCLE_PREFIX = "$";

    private final String name;
    protected final IConfigFile config;

    public static final record InitData(String name, Supplier<IConfigFile> configSupplier,
            Map<Class<?>, Object> modules) {
    }

    public FeatherModule(final InitData data) {
        super(data.modules);
        this.name = data.name;
        this.config = data.configSupplier.get();
    }

    public void onEnable() throws FeatherSetupException {
        logStatus(getLogger(), "&7setup started");
        onModuleEnable();
        logStatus(getLogger(), "&aenabled");
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

    protected void onModuleEnable() throws FeatherSetupException {};

    protected abstract void onModuleDisable();
}
