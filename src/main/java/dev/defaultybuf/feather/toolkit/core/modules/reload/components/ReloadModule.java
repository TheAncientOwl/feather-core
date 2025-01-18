/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadModule.java
 * @author Alexandru Delegeanu
 * @version 0.4
 * @description Dummy module for registering @see ReloadCommand.java
 */

package dev.defaultybuf.feather.toolkit.core.modules.reload.components;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.core.modules.reload.interfaces.IReloadModule;
import dev.defaultybuf.feather.toolkit.exceptions.FeatherSetupException;

public class ReloadModule extends FeatherModule implements IReloadModule {

    public ReloadModule(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {}

    @Override
    protected void onModuleDisable() {}

}
