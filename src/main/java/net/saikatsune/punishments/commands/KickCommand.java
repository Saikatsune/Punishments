package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();
    private String reason = "";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("kick")) {
            if(args.length >= 2) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    Player player = Bukkit.getPlayer(args[0]);

                    if(player != null) {
                        for (int i = 1; i < args.length; i++) {
                            reason = reason + " " + args[i];
                        }

                        player.kickPlayer(ChatColor.RED + "You have been kicked for" + reason + ". \n\n" +
                                ChatColor.RED + "Kicked by: " + sender.getName());

                        Bukkit.broadcastMessage(prefix + ChatColor.RED + player.getName() + " has been kicked for" + reason  + ".");

                        reason = "";
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + args[0] + " is currently offline.");
                    }
                });
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /kick (player) (reason)");
            }
        }
        return false;
    }
}
