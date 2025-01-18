/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Argumentable.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility base class to make @see Arguments out of given class
 */
package dev.defaultybuf.feather.toolkit.testing.utils;

import org.junit.jupiter.params.provider.Arguments;

public abstract class Argumentable {
    public Arguments toArguments() {
        return Arguments.of(this);
    }
}
