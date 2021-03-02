package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class UnmuteCommand implements CommandExecutor {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("unmute")) {
            if(args.length == 1) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                    if(plugin.getDatabaseManager().isPlayerMuted(target)) {
                        plugin.getDatabaseManager().unmutePlayer(target);

                        sender.sendMessage(prefix + ChatColor.GREEN + target.getName() + " has been unmuted.");
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + args[0] + " is not muted.");
                    }
                });
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /unmute (player)");
            }
        }
        return false;
    }
}
