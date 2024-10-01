/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModule.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Dummy module for registering @see ReloadCommand.java
 */

package mc.owls.valley.net.feathercore.modules.reload.components;

import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public class ReloadModule extends FeatherModule {

    public ReloadModule(final String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(IFeatherCoreProvider core) throws FeatherSetupException {
    }

    @Override
    protected void onModuleDisable() {
    }

}
