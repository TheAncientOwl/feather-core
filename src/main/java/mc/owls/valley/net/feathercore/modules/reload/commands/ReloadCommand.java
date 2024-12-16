/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ReloadCommand.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Reload configurations command
 */

package mc.owls.valley.net.feathercore.modules.reload.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.core.interfaces.IEnabledModulesProvider;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManager;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

@SuppressWarnings("unchecked")
public class ReloadCommand extends FeatherCommand<ReloadCommand.CommandData> {
    public ReloadCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(List<FeatherModule> modules) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.reload")) {
            getInterface(ILanguage.class).message(sender, Message.General.PERMISSION_DENIED);
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

        getInterface(ILanguage.class).message(sender,
                data.modules.size() == 1 ? Message.Reload.CONFIG_RELOADED : Message.Reload.CONFIGS_RELOADED);
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            getInterface(ILanguage.class).message(sender, Message.Reload.USAGE,
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
            getInterface(ILanguage.class).message(sender, Message.Reload.USAGE,
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
        return getInterface(IEnabledModulesProvider.class).getEnabledModules();
    }

    private List<String> getEnabledModulesNames() {
        final var modules = new ArrayList<String>();
        modules.add("all");
        getEnabledModules().forEach(module -> modules.add(module.getModuleName()));
        return modules;
    }
}
