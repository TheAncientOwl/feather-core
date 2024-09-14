package mc.owls.valley.net.feathercore.api.core;

import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;

public abstract class FeatherModule {
    private final String name;

    public FeatherModule(final String name) {
        this.name = name;
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

    private void logStatus(final IFeatherLogger logger, final String message) {
        logger.info("&2" + this.name + "&8: &r" + message);

    }

    protected abstract void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException;

    protected abstract void onModuleDisable();

}
