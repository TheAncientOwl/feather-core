package mc.owls.valley.net.feathercore.modules.translation.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.translation.common.Messages;

public class LanguageCommand extends FeatherCommand<LanguageCommand.CommandData> {
    private static enum CommandType {
        INFO, LIST, CHANGE
    }

    public static record CommandData(CommandType commandType, String language) {
    }

    private IPlayersDataManager playerData = null;
    private ITranslationAccessor lang = null;
    private IPropertyAccessor translationsConfig = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.playerData = core.getPlayersDataManager();
        this.lang = core.getTranslationManager();
        this.translationsConfig = core.getConfigurationManager().getTranslationsConfigFile();
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (data.commandType) {
            case INFO:
                Message.to(sender, this.lang.getTranslation(sender, this.playerData), Messages.INFO,
                        Pair.of(Placeholder.LANGUAGE, this.playerData.getPlayerModel((OfflinePlayer) sender).language));
                break;
            case LIST:
                final var languagesConfig = this.translationsConfig.getConfigurationSection("languages");

                final StringBuilder sb = new StringBuilder();

                for (final var lang : languagesConfig.getKeys(false)) {
                    final var longForm = languagesConfig.getString(lang);
                    sb.append("\n   ").append(lang).append(": ").append(longForm);
                }

                Message.to(sender, this.lang.getTranslation(sender, this.playerData), Messages.LIST,
                        Pair.of(Placeholder.LANGUAGE, sb.toString()));
                break;
            case CHANGE:
                final var playerModel = this.playerData.getPlayerModel((OfflinePlayer) sender);
                playerModel.language = data.language;
                this.playerData.markPlayerModelForSave(playerModel);
                Message.to(sender, this.lang.getTranslation(sender, this.playerData), Messages.CHANGE_SUCCESS);
                break;
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            Message.to(sender, this.lang.getTranslation(sender, this.playerData), Messages.UNKNOWN, Messages.USAGE);
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
                Message.to(sender, this.lang.getTranslation(sender, this.playerData), Messages.UNKNOWN, Messages.USAGE);
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
