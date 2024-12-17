/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherCore.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Plugin entry point
 */

package mc.owls.valley.net.feathercore.core;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.common.util.TimeUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.exceptions.ModuleNotEnabledException;
import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;

public class FeatherCore extends JavaPlugin implements IEnabledModulesProvider {
    public static final String FEATHER_CORE_YML = "feathercore.yml";

    private ModulesManager modulesManager = new ModulesManager();
    private IFeatherLogger featherLogger = null;

    @Override
    public void onEnable() {
        final var enableStartTime = System.currentTimeMillis();

        try {
            this.featherLogger = new FeatherLogger(this.getServer().getConsoleSender());
            this.modulesManager.onEnable(this);

            final var enableFinishTime = System.currentTimeMillis();

            getFeatherLogger().info("Successfully enabled&8. (&btook&8: &b"
                    + TimeUtils.formatElapsed(enableStartTime, enableFinishTime) + "&8)");
        } catch (final FeatherSetupException | ModuleNotEnabledException e) {
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
