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
    private Economy economy = null;
    private IPropertyAccessor messages = null;

    @Override
    public void onCreate(final IFeatherCoreProvider core) {
        this.economy = core.getEconomy();
        this.messages = core.getConfigurationManager().getMessagesConfigFile()
                .getConfigurationSection(Messages.ECONOMY);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("feathercore.economy.general.balance")) {
            Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
            return true;
        }

        if (args.length != 0) { // console and players can see other player's balance
            if (args.length != 1) {
                Message.to(sender, this.messages, Messages.USAGE_INVALID, Messages.USAGE_BALANCE);
                return true;
            }

            final String playerName = args[0];
            final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

            if (!player.hasPlayedBefore()) {
                Message.to(sender, this.messages, Messages.NOT_PLAYER,
                        Pair.of(Placeholder.STRING, playerName));
                return true;
            }

            Message.to(sender, this.messages, Messages.BALANCE_OTHER,
                    Pair.of(Placeholder.PLAYER_NAME, playerName),
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance(player))));
        } else if (sender instanceof Player) { // players can see their own balance
            if (!sender.hasPermission("feathercore.economy.general.balance")) {
                Message.to(sender, this.messages, Messages.PERMISSION_DENIED);
                return true;
            }

            Message.to(sender, this.messages, Messages.BALANCE_SELF,
                    Pair.of(Placeholder.BALANCE, this.economy.format(this.economy.getBalance((Player) sender))));
        } else { // console can't see its own balance (lol)
            Message.to(sender, this.messages, Messages.COMMAND_SENDER_NOT_PLAYER);
            return true;
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

}
