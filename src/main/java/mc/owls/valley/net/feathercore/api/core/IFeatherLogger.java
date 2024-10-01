/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherLogger.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Plugin logger interface
 */

package mc.owls.valley.net.feathercore.api.core;

public interface IFeatherLogger {
    public void info(final String message);

    public void warn(final String message);

    public void error(final String message);

    public void debug(final String message);

    public boolean isInitialized();
}
