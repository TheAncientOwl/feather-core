/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ILanguage.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Language module interface
 */

package mc.owls.valley.net.feathercore.modules.language.interfaces;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface ILanguage {
    public void message(final CommandSender receiver, final String key);

    public void message(final CommandSender receiver, String... keys);

    public void message(final CommandSender receiver, final String key,
            @SuppressWarnings("unchecked") Pair<String, Object>... placeholders);

    public IConfigFile getTranslation(final CommandSender sender);

    public IConfigFile getConfig();
}
