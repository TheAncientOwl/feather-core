/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Dummy2.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Dummy class for testing
 */
package mc.owls.valley.net.feathercore.api.core.dummies;

public class Dummy2 {
    private String data;

    public Dummy2() {
        this.data = "nodata2";
    }

    public Dummy2(String data) {
        this.data = data;
    }

    public void foo() {}

    public String bar() {
        return "bar2";
    };

    public String get() {
        return this.data;
    }
}
