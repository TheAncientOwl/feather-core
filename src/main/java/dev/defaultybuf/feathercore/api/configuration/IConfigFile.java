/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IConfigFile.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Configuration file interface
 */

package dev.defaultybuf.feathercore.api.configuration;

import java.io.IOException;

public interface IConfigFile extends IPropertyAccessor {
    public void saveDefaultConfig();

    public void saveConfig() throws IOException;

    public void loadConfig();

    public void reloadConfig();

    public String getFileName();
}
