/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IFeatherLoggerProvider.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Helper interface
 */

package mc.owls.valley.net.feathercore.core.interfaces;

import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;

public interface IFeatherLoggerProvider {
    public IFeatherLogger getFeatherLogger();
}
