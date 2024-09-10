package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;

public class PayToggleCommand implements IFeatherCommand {
    private IPlayersDataManager playersData = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.playersData = core.getPlayersDataManager();
        this.messages = core.getConfigurationManager().getMessagesConfigFile()
                .getConfigurationSection(Messages.ECONOMY);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("feathercore.economy.general.paytoggle")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return true;
        }

        if (!(sender instanceof Player)) {
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 0) {
            Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_PAY);
            return true;
        }

        final PlayerModel playerModel = this.playersData.getPlayerModel((Player) sender);

        if (playerModel == null) {
            return false;
        }

        playerModel.acceptsPayments = !playerModel.acceptsPayments;
        this.playersData.markPlayerModelForSave(playerModel);

        Message.to(sender, this.messages,
                playerModel.acceptsPayments ? Messages.PAY_TOGGLE_TRUE : Messages.PAY_TOGGLE_FALSE);

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
        List<String> completions = new ArrayList<>();

        return completions;
    }

}
