/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommand.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Reload configurations command
 */

package mc.owls.valley.net.feathercore.modules.reload.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.configuration.interfaces.IConfigurationManager;
import mc.owls.valley.net.feathercore.modules.reload.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class ReloadCommand extends FeatherCommand<ReloadCommand.CommandData> {
    private static enum Module {
        ALL,
        DATA,
        ECONOMY,
        RESTRICTED_PVP,
        LOOT_CHESTS,
        LANGUAGE,
        TRANSLATIONS
    }

    public static record CommandData(Module module) {
    }

    private TranslationManager lang = null;
    private IConfigurationManager configManager = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.lang = core.getTranslationManager();
        this.configManager = core.getConfigurationManager();
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        final Map<Module, Runnable> modules = Map.of(
                Module.DATA, () -> {
                    this.configManager.getDataConfiguration().reloadConfig();
                },
                Module.ECONOMY, () -> {
                    this.configManager.getEconomyConfigFile().reloadConfig();
                },
                Module.LOOT_CHESTS, () -> {
                    this.configManager.getLootChestsConfigFile().reloadConfig();
                },
                Module.RESTRICTED_PVP, () -> {
                    this.configManager.getLootChestsConfigFile().reloadConfig();
                },
                Module.TRANSLATIONS, () -> {
                    this.configManager.getTranslationsConfigFile().reloadConfig();
                },
                Module.LANGUAGE, () -> {
                    this.lang.reloadTranslations();
                });

        if (data.module.equals(Module.ALL)) {
            modules.forEach((module, reload) -> {
                reload.run();
            });

            this.lang.message(sender, Message.CONFIGS_RELOADED);
            return;
        }

        final var runnable = modules.get(data.module);
        if (runnable != null) {
            runnable.run();
            this.lang.message(sender, Message.CONFIG_RELOADED);
        } else {
            this.lang.message(sender,
                    "&8[&4Feather&cCore&8] &cMissing handler for specified option, please notify the plugin developer");
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("feathercore.reload")) {
            this.lang.message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (args.length != 1) {
            this.lang.message(sender, Message.USAGE, Pair.of(Placeholder.STRING, String.join("|", getModules())));
            return null;
        }

        Module module = null;
        try {
            module = Module.valueOf(args[0].toUpperCase());
        } catch (final Exception e) {
            this.lang.message(sender, Message.USAGE);
            return null;
        }

        return new CommandData(module);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions = StringUtils.filterStartingWith(getModules(), args[0]);
        }

        return completions;
    }

    private static List<String> getModules() {
        return Arrays.stream(Module.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

}
