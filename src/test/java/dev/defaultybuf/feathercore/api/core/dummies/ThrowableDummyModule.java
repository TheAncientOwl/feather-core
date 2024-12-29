/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ThrowableDummyModule.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Dummy module class that throws on enable for testing
 */
package dev.defaultybuf.feathercore.api.core.dummies;

import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;

public class ThrowableDummyModule extends FeatherModule {

    public ThrowableDummyModule(final FeatherModule.InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        throw new FeatherSetupException("[Test] Failed to setup ThrowableDummyModule");
    }

    @Override
    protected void onModuleDisable() {}

}
