/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file DummyModule.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Dummy module class for testing
 */
package dev.defaultybuf.feathercore.api.core.dummies;

import dev.defaultybuf.feathercore.api.core.FeatherModule;
import dev.defaultybuf.feathercore.api.exceptions.FeatherSetupException;

public class DummyModule extends FeatherModule {

    public DummyModule(final FeatherModule.InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {}

    @Override
    protected void onModuleDisable() {}

}
