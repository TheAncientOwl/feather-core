/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommand.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Reload configurations command
 */

package mc.owls.valley.net.feathercore.modules.reload.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.reload.common.Message;

public class ReloadCommand extends FeatherCommand<ReloadCommand.CommandData> {
    public static record CommandData(List<FeatherModule> modules) {
    }

    private IFeatherCoreProvider core = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.core = core;
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.reload")) {
            this.core.getLanguageManager().message(sender, Message.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        for (final var module : data.modules) {
            final var config = module.getConfig();
            if (config != null) {
                config.reloadConfig();
            }

            if (module instanceof LanguageManager) {
                ((LanguageManager) module).reloadTranslations();
            }
        }

        this.core.getLanguageManager().message(sender,
                data.modules.size() == 1 ? Message.CONFIG_RELOADED : Message.CONFIGS_RELOADED);
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            this.core.getLanguageManager().message(sender, Message.USAGE,
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
            this.core.getLanguageManager().message(sender, Message.USAGE,
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
