package mc.owls.valley.net.feathercore.api.module.interfaces;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface ITranslationAccessor {
    public IConfigFile getTranslation(final String language);

    public IConfigFile getTranslation(final CommandSender sender, final IPlayersDataManager playersData);
}
