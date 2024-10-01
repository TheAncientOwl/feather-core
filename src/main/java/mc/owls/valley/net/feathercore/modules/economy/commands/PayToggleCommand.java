/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PayToggleCommand.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Toggle on/off receiving in-game payments
 */

package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.modules.translation.components.TranslationManager;

public class PayToggleCommand extends FeatherCommand<PayToggleCommand.CommandData> {
    public static record CommandData(PlayerModel playerModel) {
    }

    private IPlayersData playersData = null;
    private TranslationManager lang = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.playersData = core.getPlayersDataManager();
        this.lang = core.getTranslationManager();
        this.playersData = core.getPlayersDataManager();
    }

    @Override
    protected void execute(final CommandSender sender, final CommandData data) {
        data.playerModel.acceptsPayments = !data.playerModel.acceptsPayments;
        this.playersData.markPlayerModelForSave(data.playerModel);

        this.lang.message(sender,
                data.playerModel.acceptsPayments ? Message.PAY_TOGGLE_TRUE : Message.PAY_TOGGLE_FALSE);
    }

    protected CommandData parse(final CommandSender sender, final String args[]) {
        // 3. check the basics
        if (!sender.hasPermission("feathercore.economy.general.paytoggle")) {
            this.lang.message(sender, Message.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            this.lang.message(sender, Message.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 0) {
            this.lang.message(sender, Message.USAGE_INVALID, Message.USAGE_PAY);
            return null;
        }

        // 2. check if player data is stored
        final PlayerModel playerModel = this.playersData.getPlayerModel((Player) sender);
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
