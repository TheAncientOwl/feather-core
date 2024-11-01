/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageCommand.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Manage player's messages language
 */

package mc.owls.valley.net.feathercore.modules.language.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.language.events.LanguageChangeEvent;

public class LanguageCommand extends FeatherCommand<LanguageCommand.CommandData> {
    private static enum CommandType {
        INFO, LIST, CHANGE
    }

    public static record CommandData(CommandType commandType, String language) {
    }

    private IPlayersData playerData = null;
    private LanguageManager lang = null;
    private IPropertyAccessor translationsConfig = null;
    private PluginManager pluginManager = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.playerData = core.getPlayersData();
        this.lang = core.getLanguageManager();
        this.translationsConfig = core.getLanguageManager().getConfig();
        this.pluginManager = core.getPlugin().getServer().getPluginManager();
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (data.commandType) {
            case INFO: {
                final var playerLangPrefix = this.playerData.getPlayerModel((OfflinePlayer) sender).language;
                final var langExtended = this.translationsConfig.getConfigurationSection("languages")
                        .getString(playerLangPrefix, "");
                this.lang.message(sender, Message.Language.INFO, Pair.of(Placeholder.LANGUAGE, langExtended));
                break;
            }
            case LIST: {
                final var langConfig = this.translationsConfig.getConfigurationSection("languages");
                final StringBuilder sb = new StringBuilder();
                for (final var lang : langConfig.getKeys(false)) {
                    final var longForm = langConfig.getString(lang);
                    sb.append("\n   ").append(lang).append(": ").append(longForm);
                }

                this.lang.message(sender, Message.Language.LIST, Pair.of(Placeholder.LANGUAGE, sb.toString()));
                break;
            }
            case CHANGE: {
                final var playerModel = this.playerData.getPlayerModel((OfflinePlayer) sender);
                playerModel.language = data.language;
                this.playerData.markPlayerModelForSave(playerModel);
                this.lang.message(sender, Message.Language.CHANGE_SUCCESS);

                this.pluginManager.callEvent(
                        new LanguageChangeEvent((Player) sender, data.language, this.lang.getTranslation(sender)));
                break;
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            this.lang.message(sender, Message.Language.UNKNOWN, Message.Language.USAGE);
            return null;
        }

        String language = null;
        CommandType commandType = null;

        final var option = args[0].toLowerCase();
        if (option.equals("info")) {
            commandType = CommandType.INFO;
        } else if (option.equals("list")) {
            commandType = CommandType.LIST;
        } else {
            if (!this.translationsConfig.getConfigurationSection("languages").getKeys(false).contains(option)) {
                this.lang.message(sender, Message.Language.UNKNOWN, Message.Language.USAGE);
                return null;
            }
            commandType = CommandType.CHANGE;
            language = option;
        }

        return new CommandData(commandType, language);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        final var languages = new ArrayList<>(
                this.translationsConfig.getConfigurationSection("languages").getKeys(false));
        languages.add("info");
        languages.add("list");

        if (args.length == 1) {
            completions = StringUtils.filterStartingWith(languages, args[0]);
        } else {
            completions = languages;
        }

        return completions;
    }

}
