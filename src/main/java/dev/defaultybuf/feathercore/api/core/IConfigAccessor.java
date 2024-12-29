/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IConfigAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Config accessor interface
 */

package dev.defaultybuf.feathercore.api.core;

import dev.defaultybuf.feathercore.api.configuration.IConfigFile;

public interface IConfigAccessor {
    public IConfigFile getConfig();
}
