/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageCommand.java
 * @author Alexandru Delegeanu
 * @version 0.9
 * @description Manage player's messages language
 */

package dev.defaultybuf.feathercore.modules.language.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.common.util.StringUtils;
import dev.defaultybuf.feathercore.api.core.FeatherCommand;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.language.events.LanguageChangeEvent;

public class LanguageCommand extends FeatherCommand<LanguageCommand.CommandData> {
    public LanguageCommand(final InitData data) {
        super(data);
    }

    public static enum CommandType {
        INFO, LIST, CHANGE
    }

    public static record CommandData(CommandType commandType, String language) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (data.commandType) {
            case INFO: {
                final var playerLangPrefix = getInterface(IPlayersData.class)
                        .getPlayerModel((OfflinePlayer) sender).language;
                final var langExtended =
                        getLanguage().getConfig().getConfigurationSection("languages")
                                .getString(playerLangPrefix, "");
                getLanguage().message(sender, Message.Language.INFO,
                        Pair.of(Placeholder.LANGUAGE, langExtended));
                break;
            }
            case LIST: {
                final var langConfig =
                        getLanguage().getConfig().getConfigurationSection("languages");
                final StringBuilder sb = new StringBuilder();
                for (final var lang : langConfig.getKeys(false)) {
                    final var longForm = langConfig.getString(lang);
                    sb.append("\n   ").append(lang).append(": ").append(longForm);
                }

                getLanguage().message(sender, Message.Language.LIST,
                        Pair.of(Placeholder.LANGUAGE, sb.toString()));
                break;
            }
            case CHANGE: {
                final var playerModel =
                        getInterface(IPlayersData.class).getPlayerModel((OfflinePlayer) sender);
                playerModel.language = data.language;
                getInterface(IPlayersData.class).markPlayerModelForSave(playerModel);
                getLanguage().message(sender, Message.Language.CHANGE_SUCCESS);

                getPlugin().getServer().getPluginManager().callEvent(
                        new LanguageChangeEvent((Player) sender, data.language,
                                getLanguage().getTranslation(sender)));
                break;
            }
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            getLanguage().message(sender, Message.Language.UNKNOWN, Message.Language.USAGE);
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
            if (!getLanguage().getConfig().getConfigurationSection("languages").getKeys(false)
                    .contains(option)) {
                getLanguage().message(sender, Message.Language.UNKNOWN, Message.Language.USAGE);
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
                getLanguage().getConfig().getConfigurationSection("languages").getKeys(false));
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
