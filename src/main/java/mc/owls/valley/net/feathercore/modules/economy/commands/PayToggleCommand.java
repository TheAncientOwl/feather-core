package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.data.IPlayersDataManager;
import mc.owls.valley.net.feathercore.api.data.mongo.models.PlayerModel;
import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.economy.common.Message;
import mc.owls.valley.net.feathercore.utils.ChatUtils;

public class PayToggleCommand implements IFeatherCommand {
    private IPlayersDataManager playersData = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final FeatherCore plugin) {
        this.playersData = plugin.getPlayersDataManager();
        this.messages = plugin.getConfigurationManager().getMessagesConfigFile().getConfigurationSection("economy");
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label,
            final String[] args) {
        if (!commandSender.hasPermission("feathercore.economy.general.paytoggle")) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.PERMISSION_DENIED);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.COMMAND_SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length != 0) {
            ChatUtils.sendMessage(commandSender, this.messages, Message.USAGE_INVALID, Message.USAGE_PAY);
            return true;
        }

        final PlayerModel playerModel = this.playersData.getPlayerModel((Player) commandSender);

        if (playerModel == null) {
            return false;
        }

        playerModel.acceptsPayments = !playerModel.acceptsPayments;
        this.playersData.markPlayerModelForSave(playerModel);

        ChatUtils.sendMessage(commandSender, this.messages,
                playerModel.acceptsPayments ? Message.PAY_TOGGLE_TRUE : Message.PAY_TOGGLE_FALSE);

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
            final String alias, final String[] args) {
        List<String> completions = new ArrayList<>();

        return completions;
    }

}
