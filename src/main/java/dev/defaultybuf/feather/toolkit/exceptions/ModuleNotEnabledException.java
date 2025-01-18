/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ModuleNotEnabledException.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Exception thrown if a disabled module is accessed
 */

package dev.defaultybuf.feather.toolkit.exceptions;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException() {
        super();
    }

    public ModuleNotEnabledException(String moduleName) {
        super("Module '" + moduleName + "' is not enabled");
    }

}
