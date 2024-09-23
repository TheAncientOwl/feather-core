package mc.owls.valley.net.feathercore.api.core;

public interface IFeatherLogger {
    public void info(final String message);

    public void warn(final String message);

    public void error(final String message);

    public void debug(final String message);

    public boolean isInitialized();
}
