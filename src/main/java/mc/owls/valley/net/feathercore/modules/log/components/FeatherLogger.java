package mc.owls.valley.net.feathercore.modules.log.components;

import org.bukkit.command.ConsoleCommandSender;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;

public class FeatherLogger extends FeatherModule implements IFeatherLogger {
    public static final String PLUGIN_TAG = "&8[&eFeather&6Core&8]&r ";

    private ConsoleCommandSender console = null;

    public FeatherLogger(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.console = core.getPlugin().getServer().getConsoleSender();
    }

    @Override
    protected void onModuleDisable() {
    }

    public void info(final String message) {
        sendMessage("&8» &3" + message);
    }

    public void warn(final String message) {
        sendMessage("&8» &e" + message);
    }

    public void error(final String message) {
        sendMessage("&8» &c" + message);
    }

    public void debug(final String message) {
        sendMessage("&8» &a" + message);
    }

    private void sendMessage(final String message) {
        this.console.sendMessage(StringUtils.translateColors(PLUGIN_TAG + message));
    }

    @Override
    public boolean isInitialized() {
        return this.console != null;
    }
}
