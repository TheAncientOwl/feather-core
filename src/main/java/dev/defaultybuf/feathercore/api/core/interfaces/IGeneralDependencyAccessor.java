/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file IGeneralDependencyAccessor.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description General dependencies for all modules
 */

package dev.defaultybuf.feathercore.api.core.interfaces;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.core.IFeatherLogger;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

public interface IGeneralDependencyAccessor {
    public JavaPlugin getPlugin() throws IllegalStateException;

    public IFeatherLogger getLogger() throws IllegalStateException;

    public List<FeatherModule> getEnabledModules() throws IllegalStateException;

    public ILanguage getLanguage() throws IllegalStateException;
}
