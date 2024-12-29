
/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ActualModule.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Marker annotation for actual modules, used for testing
 */
package dev.defaultybuf.feathercore.modules.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.defaultybuf.feathercore.modules.common.DependencyInjector;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ActualModule {
    DependencyInjector.Module type();

    Resource[] resources();
}
