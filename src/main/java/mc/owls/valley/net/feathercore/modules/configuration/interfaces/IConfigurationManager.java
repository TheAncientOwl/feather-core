/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IConfigurationManager.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Configuration files accessor interface
 */

package mc.owls.valley.net.feathercore.modules.configuration.interfaces;

public interface IConfigurationManager {
    public IConfigFile getDataConfiguration();

    public IConfigFile getEconomyConfigFile();

    public IConfigFile getPvPConfigFile();

    public IConfigFile getTranslationsConfigFile();

    public IConfigFile getLootChestsConfigFile();
}
