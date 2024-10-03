/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommand.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Reload configurations command
 */

package mc.owls.valley.net.feathercore.modules.reload.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.util.Pair;
import mc.owls.valley.net.feathercore.api.common.util.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.reload.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class ReloadCommand extends FeatherCommand<ReloadCommand.CommandData> {
    public static record CommandData(List<FeatherModule> modules) {
    }

    private IFeatherCoreProvider core = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.core = core;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        for (final var module : data.modules) {
            final var config = module.getConfig();
            if (config != null) {
                config.reloadConfig();
            }

            if (module instanceof TranslationManager) {
                ((TranslationManager) module).reloadTranslations();
            }
        }

        this.core.getTranslationManager().message(sender,
                data.modules.size() == 1 ? Message.CONFIG_RELOADED : Message.CONFIGS_RELOADED);
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("feathercore.reload")) {
            this.core.getTranslationManager().message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (args.length != 1) {
            this.core.getTranslationManager().message(sender, Message.USAGE,
                    Pair.of(Placeholder.STRING, getEnabledModulesNames()));
            return null;
        }

        List<FeatherModule> modules = new ArrayList<FeatherModule>();

        final var arg0 = args[0].toLowerCase();
        if (arg0.equals("all")) {
            modules = getEnabledModules();
        } else {
            for (final var module : getEnabledModules()) {
                if (module.getModuleName().toLowerCase().equals(arg0)) {
                    modules.add(module);
                    break;
                }
            }
        }

        if (modules.isEmpty()) {
            this.core.getTranslationManager().message(sender, Message.USAGE,
                    Pair.of(Placeholder.STRING, getEnabledModulesNames()));
            return null;
        }

        return new CommandData(modules);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions = StringUtils.filterStartingWith(getEnabledModulesNames(), args[0]);
        }

        return completions;
    }

    private List<FeatherModule> getEnabledModules() {
        return this.core.getEnabledModules();
    }

    private List<String> getEnabledModulesNames() {
        final var modules = new ArrayList<String>();
        modules.add("all");
        this.core.getEnabledModules().forEach(module -> modules.add(module.getModuleName()));
        return modules;
    }

}
