/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ILanguage.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Language module interface
 */

package dev.defaultybuf.feather.toolkit.core.modules.language.interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import dev.defaultybuf.feather.toolkit.api.configuration.IConfigFile;
import dev.defaultybuf.feather.toolkit.util.java.Pair;

public interface ILanguage {
    public void message(final CommandSender receiver, final String key);

    public void message(final CommandSender receiver, String... keys);

    public void message(final CommandSender receiver, final String key,
            Pair<String, Object> placeholder);

    public void message(final CommandSender receiver, final String key,
            final List<Pair<String, Object>> placeholders);

    public IConfigFile getTranslation(final CommandSender sender);

    public IConfigFile getConfig();
}
