package mc.owls.valley.net.feathercore.api.core.module;

import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;

public abstract class FeatherModule {
    private final String name;

    public FeatherModule(final String name) {
        this.name = name;
    }

    public ModuleEnableStatus onEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        final IFeatherLogger logger = core.getFeatherLogger();

        logStatus(logger, "&7setup started");

        final var status = onModuleEnable(core);

        switch (status) {
            case SUCCESS:
                logStatus(logger, "&aenabled");
                break;
            case FAIL:
                logStatus(logger, "&cfailed to enable");
                break;
            case NOT_ENABLED:
                logStatus(logger, "&enot enabled");
                break;
            default:
                logStatus(logger, "&eenabling phase finished with unhandled status. Please contact the developer");
                break;
        }

        return status;
    }

    public void onDisable(final IFeatherLogger logger) {
        logStatus(logger, "&7disabling started");
        onModuleDisable();
        logStatus(logger, "&adisabled");
    }

    private void logStatus(final IFeatherLogger logger, final String message) {
        logger.info("&2" + this.name + "&8: &r" + message);

    }

    protected abstract ModuleEnableStatus onModuleEnable(final IFeatherCoreProvider core)
            throws FeatherSetupException;

    protected abstract void onModuleDisable();
}
