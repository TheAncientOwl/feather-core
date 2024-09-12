package mc.owls.valley.net.feathercore.modules.economy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.common.Message;
import mc.owls.valley.net.feathercore.api.common.Pair;
import mc.owls.valley.net.feathercore.api.common.Placeholder;
import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.configuration.IPropertyAccessor;
import mc.owls.valley.net.feathercore.api.core.IFeatherCommand;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.modules.economy.common.Messages;
import net.milkbowl.vault.economy.Economy;

public class BalanceCommand implements IFeatherCommand {
    private static enum CommandType {
        SELF, OTHER
    }

    private static record CommandData(CommandType commandType, OfflinePlayer other) {
    }

    private Economy economy = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.economy = core.getEconomy();
        this.messages = core.getConfigurationManager().getMessagesConfigFile()
                .getConfigurationSection(Messages.ECONOMY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final CommandData data = parse(sender, args);

        if (data == null) {
            return true;
        }

        switch (data.commandType) {
            case SELF:
                Message.to(sender, this.messages, Messages.BALANCE_SELF,
                        Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));
                break;
            case OTHER:
                Message.to(sender, this.messages, Messages.BALANCE_OTHER,
                        Pair.of(Placeholder.PLAYER_NAME, data.other.getName()),
                        Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance(data.other))));

                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias,
            final String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final var arg = args[0];
            final List<String> onlinePlayers = StringUtils.getOnlinePlayers();

            if (arg.isEmpty()) {
                completions = onlinePlayers;
            } else {
                completions = StringUtils.filterStartingWith(onlinePlayers, arg);
            }
        }

        return completions;
    }

    @SuppressWarnings("unchecked")
    private CommandData parse(final CommandSender sender, final String[] args) {
        CommandType commandType = null;
        OfflinePlayer targetPlayer = null;

        if (!sender.hasPermission("feathercore.economy.general.balance")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return null;
        }

        if (args.length != 0) {
            if (args.length == 1) {
                Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_BALANCE);
                return null;
            }

            commandType = CommandType.OTHER;
            targetPlayer = Bukkit.getOfflinePlayer(args[0]);

            if (!targetPlayer.hasPlayedBefore()) {
                Message.to(sender, this.messages, Messages.NOT_PLAYER,
                        Pair.of(Placeholder.STRING, args[0]));
                return null;
            }
        } else if (sender instanceof Player) {
            commandType = CommandType.SELF;
        } else {
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return null;
        }

        return new CommandData(commandType, targetPlayer);
    }

}
