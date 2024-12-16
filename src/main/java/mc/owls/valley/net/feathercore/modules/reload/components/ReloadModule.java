/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModule.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Dummy module for registering @see ReloadCommand.java
 */

package mc.owls.valley.net.feathercore.modules.reload.components;

import java.util.function.Supplier;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.modules.reload.interfaces.IReloadModule;

public class ReloadModule extends FeatherModule implements IReloadModule {

    public ReloadModule(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(IFeatherCoreProvider core) throws FeatherSetupException {
    }

    @Override
    protected void onModuleDisable() {
    }

}
