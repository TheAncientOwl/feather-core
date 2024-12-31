/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherCore.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Plugin entry point
 */

package dev.defaultybuf.feathercore.core;

import java.io.IOException;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.common.util.Clock;
import dev.defaultybuf.feathercore.api.common.util.TimeUtils;
import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.core.IFeatherLogger;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.api.exceptions.ModuleNotEnabledException;
import dev.defaultybuf.feathercore.core.interfaces.IEnabledModulesProvider;

public class FeatherCore extends JavaPlugin implements IEnabledModulesProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private ModulesManager modulesManager = new ModulesManager();
    private IFeatherLogger featherLogger = null;

    @Override
    public void onEnable() {
        final var enableStartTime = Clock.currentTimeMillis();

        try {
            this.featherLogger = new FeatherLogger(this.getServer().getConsoleSender());
            this.modulesManager.onEnable(this);

            final var enableFinishTime = Clock.currentTimeMillis();

            getFeatherLogger().info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatElapsed(enableStartTime, enableFinishTime) + "&8)");
        } catch (final FeatherSetupException | ModuleNotEnabledException | IOException e) {
            this.featherLogger.error(StringUtils.exceptionToStr(e));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        this.modulesManager.onDisable(getFeatherLogger());
        getFeatherLogger().info("Goodbye&8!");
    }

    public IFeatherLogger getFeatherLogger() {
        return this.featherLogger;
    }

    @Override
    public List<FeatherModule> getEnabledModules() {
        return this.modulesManager.getEnabledModules();
    }
}
