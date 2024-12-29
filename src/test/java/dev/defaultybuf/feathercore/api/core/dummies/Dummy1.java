/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Dummy1.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Dummy class for testing
 */
package dev.defaultybuf.feathercore.api.core.dummies;

public class Dummy1 {
    private String data;

    public Dummy1() {
        this.data = "nodata1";
    }

    public Dummy1(String data) {
        this.data = data;
    }

    public void foo() {}

    public String bar() {
        return "bar1";
    };

    public String get() {
        return this.data;
    }
}
