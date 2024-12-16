/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ILanguage.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Language module interface
 */

package mc.owls.valley.net.feathercore.modules.language.interfaces;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;

public interface ILanguage {
    public void message(final CommandSender receiver, final String key);

    public void message(final CommandSender receiver, String... keys);

    @SuppressWarnings("unchecked")
    public void message(final CommandSender receiver, final String key, Pair<String, Object>... placeholders);
}
