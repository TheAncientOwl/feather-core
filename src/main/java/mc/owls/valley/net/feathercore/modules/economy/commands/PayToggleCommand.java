/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PayToggleCommand.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @description Toggle on/off receiving in-game payments
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

public class PayToggleCommand extends FeatherCommand<PayToggleCommand.CommandData> {
    public PayToggleCommand(final InitData data) {
        super(data);
    }

    public static record CommandData(PlayerModel playerModel) {
    }

    @Override
    protected boolean hasPermission(final CommandSender sender, final CommandData data) {
        if (!sender.hasPermission("feathercore.economy.general.paytoggle")) {
            getInterface(ILanguage.class).message(sender, Message.General.PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        data.playerModel.acceptsPayments = !data.playerModel.acceptsPayments;
        getInterface(IPlayersData.class).markPlayerModelForSave(data.playerModel);

        getInterface(ILanguage.class).message(sender,
                data.playerModel.acceptsPayments ? Message.Economy.PAY_TOGGLE_TRUE : Message.Economy.PAY_TOGGLE_FALSE);
    }

    protected CommandData parse(final CommandSender sender, final String args[]) {
        // 3. check the basics
        if (!(sender instanceof Player)) {
            getInterface(ILanguage.class).message(sender, Message.General.PLAYERS_ONLY);
            return null;
        }

        if (args.length != 0) {
            getInterface(ILanguage.class).message(sender, Message.General.USAGE_INVALID, Message.Economy.USAGE_PAY);
            return null;
        }

        // 2. check if player data is stored
        final PlayerModel playerModel = getInterface(IPlayersData.class).getPlayerModel((Player) sender);
        if (playerModel == null) {
            return null;
        }

        return new CommandData(playerModel);
    }

    @Override
    public List<String> onTabComplete(final String[] args) {
        List<String> completions = new ArrayList<>();

        return completions;
    }

}
