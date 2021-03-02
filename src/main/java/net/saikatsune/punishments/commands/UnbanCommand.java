package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class UnbanCommand implements CommandExecutor {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("unban")) {
            if(args.length == 1) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                    if(plugin.getDatabaseManager().isPlayerBanned(target)) {
                        plugin.getDatabaseManager().unbanPlayer(target);

                        sender.sendMessage(prefix + ChatColor.GREEN + target.getName() + " has been unbanned.");
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + args[0] + " is not banned.");
                    }
                });
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /unban (player)");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /unban (player)");
        }
        return false;
    }
}
