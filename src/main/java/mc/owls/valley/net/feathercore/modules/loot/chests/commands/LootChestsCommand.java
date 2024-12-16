/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LootChestsCommand.java
 * @author Alexandru Delegeanu
 * @version 0.7
 * @description Module main command
 */

package mc.owls.valley.net.feathercore.modules.loot.chests.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.common.util.StringUtils;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;
import mc.owls.valley.net.feathercore.modules.loot.chests.interfaces.ILootChests;

public class LootChestsCommand extends FeatherCommand<LootChestsCommand.CommandData> {
    public LootChestsCommand(final InitData data) {
        super(data);
    }

    private static enum CommandType {
        SET, UNSET, CREATE, DELETE, INFO, LOCATIONS
    }

    public static record CommandData(CommandType commandType, String chestType, String displayName, long cooldown,
            Chest chest) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.lootchests")) {
            getInterface(ILanguage.class).message(sender, Message.General.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        switch (data.commandType) {
            case SET:
                executeSet(sender, data);
                break;
            case UNSET:
                executeUnset(sender, data);
                break;
            case CREATE:
                executeCreate(sender, data);
                break;
            case DELETE:
                executeDelete(sender, data);
                break;
            case INFO:
                executeInfo(sender, data);
                break;
            case LOCATIONS:
                executeLocations(sender, data);
                break;
        }
    }

    protected CommandData parse(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.LootChests.USAGE);
            return null;
        }

        String chestType = null;
        String displayName = null;
        long cooldown = 0;
        Chest chest = null;

        final CommandType commandType = java.util.Map.of(
                "set", CommandType.SET,
                "unset", CommandType.UNSET,
                "create", CommandType.CREATE,
                "delete", CommandType.DELETE,
                "info", CommandType.INFO,
                "locations", CommandType.LOCATIONS).get(args[0].toLowerCase());

        if (commandType == null) {
            getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.LootChests.USAGE);
            return null;
        }

        // set, delete, locations -> registered chestType
        if (Set.of(CommandType.SET, CommandType.DELETE, CommandType.LOCATIONS)
                .contains(commandType)) {
            if (args.length < 2) {
                getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.LootChests.USAGE);
                return null;
            }

            if (!getInterface(ILootChests.class).isChestType(args[1])) {
                getInterface(ILanguage.class).message(sender, Message.LootChests.NOT_A_REGISTERED_CHEST,
                        Pair.of(Placeholder.STRING, args[1]));
                return null;
            }

            chestType = args[1];
        }

        // create -> chestType, displayName, cooldown
        if (commandType == CommandType.CREATE && args.length != 4) {
            getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.LootChests.USAGE);
            return null;
        } else if (commandType == CommandType.CREATE) {
            chestType = args[1];
            displayName = args[2];

            try {
                cooldown = Long.parseLong(args[3]);
            } catch (final Exception e) {
                getInterface(ILanguage.class).message(sender, Message.General.NAN,
                        Pair.of(Placeholder.STRING, args[3]));
                return null;
            }
        }

        // set, unset, create, info -> chest
        if (Set.of(CommandType.SET, CommandType.UNSET, CommandType.CREATE, CommandType.INFO).contains(commandType)) {
            if (!(sender instanceof Player)) {
                getInterface(ILanguage.class).message(sender, Message.General.PLAYERS_ONLY);
                return null;
            }

            final Block targetedBlock = ((Player) sender).getTargetBlock((Set<Material>) null, 5);

            if (targetedBlock.getType() != Material.CHEST) {
                getInterface(ILanguage.class).message(sender, Message.LootChests.NOT_A_CHEST,
                        Pair.of(Placeholder.MISC, targetedBlock.getType().toString()));
                return null;
            }

            chest = (Chest) targetedBlock.getState();
        }

        if (chestType == null && chest != null) {
            chestType = getInterface(ILootChests.class).getChestType(chest.getLocation().toString());
        }

        return new CommandData(commandType, chestType, displayName, cooldown, chest);
    }

    private void executeSet(final CommandSender sender, final CommandData data) {
        final var chestLocation = data.chest.getLocation().toString();
        getInterface(ILootChests.class).setChest(chestLocation, data.chestType);

        getInterface(ILanguage.class).message(sender, Message.LootChests.SET_SUCCESS, List.of(
                Pair.of(Placeholder.LOCATION, chestLocation),
                Pair.of(Placeholder.TYPE, data.chestType)));
    }

    private void executeUnset(final CommandSender sender, final CommandData data) {
        final var chestLocation = data.chest.getLocation().toString();

        if (data.chestType == null) {
            getInterface(ILanguage.class).message(sender, Message.LootChests.NOT_A_REGISTERED_CHEST);
            return;
        }

        getInterface(ILootChests.class).unsetChest(chestLocation);

        getInterface(ILanguage.class).message(sender, Message.LootChests.UNSET_SUCCESS, List.of(
                Pair.of(Placeholder.LOCATION, chestLocation),
                Pair.of(Placeholder.TYPE, data.chestType)));
    }

    private void executeCreate(final CommandSender sender, final CommandData data) {
        getInterface(ILootChests.class).createChest(data.chestType, data.displayName, data.cooldown,
                data.chest.getInventory());

        getInterface(ILanguage.class).message(sender, Message.LootChests.CREATE_SUCCESS,
                Pair.of(Placeholder.TYPE, data.chestType));
    }

    private void executeDelete(final CommandSender sender, final CommandData data) {
        getInterface(ILootChests.class).deleteChest(data.chestType);

        getInterface(ILanguage.class).message(sender, Message.LootChests.DELETE_SUCCESS,
                Pair.of(Placeholder.TYPE, data.chestType));
    }

    private void executeInfo(final CommandSender sender, final CommandData data) {
        if (data.chestType == null) {
            getInterface(ILanguage.class).message(sender, Message.LootChests.NOT_A_REGISTERED_CHEST);
        } else {
            getInterface(ILanguage.class).message(sender, Message.LootChests.INFO,
                    Pair.of(Placeholder.TYPE, data.chestType));
        }
    }

    private void executeLocations(final CommandSender sender, final CommandData data) {
        final var locations = getInterface(ILootChests.class).getChestLocations(data.chestType);

        if (locations.isEmpty()) {
            getInterface(ILanguage.class).message(sender, Message.LootChests.LOCATIONS, List.of(
                    Pair.of(Placeholder.TYPE, data.chestType),
                    Pair.of(Placeholder.LOCATIONS, "none")));
            return;
        }

        final StringBuilder sb = new StringBuilder("\n  ");
        for (int index = 0; index < locations.size(); index++) {
            sb.append(index).append(". ").append(locations.get(index)).append("\n  ");
        }

        getInterface(ILanguage.class).message(sender, Message.LootChests.LOCATIONS, List.of(
                Pair.of(Placeholder.TYPE, data.chestType),
                Pair.of(Placeholder.LOCATIONS, sb.toString())));
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        final String arg0 = args.length > 0 ? args[0].toLowerCase() : "";

        if (args.length == 1) {
            final List<String> arg0Values = new ArrayList<>();
            arg0Values.add("set");
            arg0Values.add("unset");
            arg0Values.add("create");
            arg0Values.add("delete");
            arg0Values.add("info");
            arg0Values.add("locations");

            completions = StringUtils.filterStartingWith(arg0Values, args[0]);
        } else if (args.length == 2
                && (arg0.equals("create") || arg0.equals("set") || arg0.equals("delete") || arg0.equals("locations"))) {
            for (final var type : getInterface(ILootChests.class).getConfig().getConfigurationSection("chests")
                    .getKeys(false)) {
                completions.add(type);
            }
        } else if (arg0.equals("create")) {
            if (args.length == 3) {
                completions.add("display-name");
            } else if (args.length == 4) {
                completions.add("cooldown (seconds)");
            }
        }

        return completions;
    }

}
