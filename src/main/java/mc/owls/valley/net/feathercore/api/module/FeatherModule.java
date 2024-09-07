package mc.owls.valley.net.feathercore.api.module;

import mc.owls.valley.net.feathercore.api.IFeatherLoggger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public abstract class FeatherModule {
    private final String name;

    public FeatherModule(final String name) {
        this.name = name;
    }

    public ModuleEnableStatus onEnable(final FeatherCore plugin) throws FeatherSetupException {
        final IFeatherLoggger logger = plugin.getFeatherLogger();

        logStatus(logger, "&7enabling started...");

        final var status = onModuleEnable(plugin);

        switch (status) {
            case SUCCESS:
                logStatus(logger, "&aenabled");
                break;
            case FAIL:
                logStatus(logger, "&cfailed to enable");
                break;
            case OK_NOT_ENABLED:
                logStatus(logger, "&enot enabled");
                break;
            default:
                logStatus(logger, "&eenabling phase finished with unhandled status. Please contact the developer");
                break;
        }

        return status;
    }

    public void onDisable(final IFeatherLoggger logger) {
        logStatus(logger, "&7disabling started...");
        onModuleDisable();
        logStatus(logger, "&adisabled");
    }

    private void logStatus(final IFeatherLoggger logger, final String message) {
        logger.info("&8» &2" + this.name + "&8: &r" + message);

    }

    protected abstract ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws FeatherSetupException;

    protected abstract void onModuleDisable();
}
