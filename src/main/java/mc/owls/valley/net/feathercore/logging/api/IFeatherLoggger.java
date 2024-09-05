package mc.owls.valley.net.feathercore.logging.api;

public interface IFeatherLoggger {
    public void info(final String message);

    public void warn(final String message);

    public void error(final String message);

    public void debug(final String message);

}
