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

package mc.owls.valley.net.feathercore.modules.language.interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface ILanguage {
    public void message(final CommandSender receiver, final String key);

    public void message(final CommandSender receiver, String... keys);

    public void message(final CommandSender receiver, final String key, Pair<String, Object> placeholder);

    public void message(final CommandSender receiver, final String key,
            final List<Pair<String, Object>> placeholders);

    public IConfigFile getTranslation(final CommandSender sender);

    public IConfigFile getConfig();
}
