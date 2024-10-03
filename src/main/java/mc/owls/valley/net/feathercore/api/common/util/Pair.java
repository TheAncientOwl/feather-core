/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Pair.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Utility class
 */

package mc.owls.valley.net.feathercore.api.common.util;

public class Pair<First, Second> {
    public First first = null;
    public Second second = null;

    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    public static <First, Second> Pair<First, Second> of(final First first, final Second second) {
        return new Pair<First, Second>(first, second);
    }
}
