package mc.owls.valley.net.feathercore.modules.translation.components;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.configuration.components.bukkit.BukkitConfigFile;

public class TranslationManager extends FeatherModule implements ITranslationAccessor {
    private Map<String, IConfigFile> translations = null;
    private JavaPlugin plugin = null;
    private IFeatherLogger logger = null;
    private IPlayersDataManager playersData = null;

    public TranslationManager(String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.plugin = core.getPlugin();
        this.logger = core.getFeatherLogger();
        this.playersData = core.getPlayersDataManager();

        this.translations = new HashMap<>();
        this.translations.put("en", loadTranslation("en"));
    }

    @Override
    protected void onModuleDisable() {
    }

    @Override
    public IConfigFile getTranslation(final String language) {
        IConfigFile translation = this.translations.get(language);

        if (translation == null) {
            translation = loadTranslation(language);
            if (translation != null) {
                this.translations.put(language, translation);
            } else {
                translation = this.translations.get("en");
            }
        }

        return translation;
    }

    @Override
    public IConfigFile getTranslation(final CommandSender sender) {
        return getTranslation(
                sender instanceof Player ? this.playersData.getPlayerModel((Player) sender).language : "en");
    }

    private IConfigFile loadTranslation(final String language) {
        IConfigFile translation = null;
        try {
            translation = new BukkitConfigFile(this.plugin, Path.of("translations", language + ".yml").toString());
        } catch (final Exception e) {
            this.logger
                    .error("Could not load translation '" + language + "'\nReason: " + StringUtils.exceptionToStr(e));
        }

        return translation;
    }

}
