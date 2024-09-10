package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.ChatUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.core.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.database.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;

public class PayToggleCommand implements IFeatherCommand {
    private IPlayersDataManager playersData = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.playersData = core.getPlayersDataManager();
        this.messages = core.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("feathercore.economy.general.paytoggle")) {
            ChatUtils.sendMessage(sender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (!(sender instanceof Player)) {
            ChatUtils.sendMessage(sender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 0) {
            ChatUtils.sendMessage(sender, this.messages, Message.USAGE_INVALID, Message.USAGE_PAY);
            return true;
        }

        final PlayerModel playerModel = this.playersData.getPlayerModel((Player) sender);

        if (playerModel == null) {
            return false;
        }

        playerModel.acceptsPayments = !playerModel.acceptsPayments;
        this.playersData.markPlayerModelForSave(playerModel);

        ChatUtils.sendMessage(sender, this.messages,
                playerModel.acceptsPayments ? Message.PAY_TOGGLE_TRUE : Message.PAY_TOGGLE_FALSE);

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
        List<String> completions = new ArrayList<>();

        return completions;
    }

}
