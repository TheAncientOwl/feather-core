/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageManager.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Module responsible for managing plugin messages translations
 */

package mc.owls.valley.net.feathercore.modules.language.components;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IFeatherLogger;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.configuration.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;

public class LanguageManager extends FeatherModule {
    private Map<String, IConfigFile> translations = null;
    private JavaPlugin plugin = null;
    private IFeatherLogger logger = null;
    private IPlayersData playersData = null;

    public LanguageManager(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.plugin = core.getPlugin();
        this.logger = core.getFeatherLogger();
        this.playersData = core.getPlayersData();

        this.translations = new HashMap<>();
        this.translations.put("en", loadTranslation("en"));
    }

    @Override
    protected void onModuleDisable() {
    }

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

    public IConfigFile getTranslation(final CommandSender sender) {
        return getTranslation(
                sender instanceof Player ? this.playersData.getPlayerModel((Player) sender).language : "en");
    }

    private IConfigFile loadTranslation(final String language) {
        IConfigFile translation = null;
        try {
            translation = new BukkitConfigFile(this.plugin, Path.of("language", language + ".yml").toString());
        } catch (final Exception e) {
            this.logger
                    .error("Could not load translation '" + language + "'\nReason: " + StringUtils.exceptionToStr(e));
        }

        return translation;
    }

    public void reloadTranslations() {
        this.translations.forEach((name, config) -> {
            config.reloadConfig();
        });
    }

    public void message(final CommandSender receiver, final String key) {
        receiver.sendMessage(StringUtils.translateColors(getTranslation(receiver).getString(key)));
    }

    public void message(final CommandSender receiver, String... keys) {
        final StringBuilder sb = new StringBuilder();

        final var translation = getTranslation(receiver);

        for (final var key : keys) {
            sb.append(translation.getString(key)).append('\n');
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        receiver.sendMessage(StringUtils.translateColors(sb.toString()));
    }

    @SafeVarargs
    public final void message(final CommandSender receiver, final String key, Pair<String, Object>... placeholders) {
        receiver
                .sendMessage(StringUtils
                        .translateColors(StringUtils.replacePlaceholders(
                                getTranslation(receiver).getString(key), placeholders)));
    }

}
