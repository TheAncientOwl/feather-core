/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file FeatherSetupException.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Exception throw during plugin load
 */

package dev.defaultybuf.feathercore.api.exceptions;

public class FeatherSetupException extends Exception {
    public FeatherSetupException() {
        super();
    }

    public FeatherSetupException(String message) {
        super(message);
    }

    public FeatherSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatherSetupException(Throwable cause) {
        super(cause);
    }
}
