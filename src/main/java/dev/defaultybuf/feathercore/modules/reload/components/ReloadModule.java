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

package dev.defaultybuf.feathercore.modules.reload.components;

import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;
import dev.defaultybuf.feathercore.modules.reload.interfaces.IReloadModule;

public class ReloadModule extends FeatherModule implements IReloadModule {

    public ReloadModule(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {}

    @Override
    protected void onModuleDisable() {}

}
