package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.core.FeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.api.module.interfaces.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationAccessor;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;

public class PayToggleCommand extends FeatherCommand<PayToggleCommand.CommandData> {
    public static record CommandData(PlayerModel playerModel) {
    }

    private IPlayersDataManager playersData = null;
    private ITranslationAccessor lang = null;

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

        Message.to(sender, this.lang.getTranslation(sender, this.playersData),
                data.playerModel.acceptsPayments ? Messages.PAY_TOGGLE_TRUE : Messages.PAY_TOGGLE_FALSE);
    }

    protected CommandData parse(final CommandSender sender, final String args[]) {
        // 3. check the basics
        if (!sender.hasPermission("feathercore.economy.general.paytoggle")) {
            Message.to(sender, this.lang.getTranslation(sender, this.playersData), Messages.PERMISSION_DENIED);
            return null;
        }

        if (!(sender instanceof Player)) {
            Message.to(sender, this.lang.getTranslation(sender, this.playersData), Messages.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        if (args.length != 0) {
            Message.to(sender, this.lang.getTranslation(sender, this.playersData), Messages.USAGE_INVALID,
                    Messages.USAGE_PAY);
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
