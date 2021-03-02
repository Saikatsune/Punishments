package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.manager.DatabaseManager;
import net.saikatsune.punishments.manager.DateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class CheckCommand implements CommandExecutor {

    private final Punishments plugin = Punishments.getInstance();

    private final DatabaseManager databaseManager = plugin.getDatabaseManager();
    private final DateManager dateManager = plugin.getDateManager();

    private final String prefix = plugin.getPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("check")) {
            if(args.length == 1) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

                    if(!databaseManager.isPlayerCached(offlinePlayer) && !databaseManager.isPlayerBanned(offlinePlayer)
                            && !databaseManager.isPlayerMuted(offlinePlayer)) {
                        sender.sendMessage(prefix + ChatColor.RED + offlinePlayer.getName() + " is not registered " +
                                "in the database.");
                    } else if(!databaseManager.isPlayerBanned(offlinePlayer) && !databaseManager.isPlayerMuted(offlinePlayer) &&
                            databaseManager.isPlayerCached(offlinePlayer)) {
                        sender.sendMessage(prefix + ChatColor.RED + offlinePlayer.getName() + " is currently not punished.");
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(prefix + ChatColor.GRAY + "Checking the user " +
                                ChatColor.RED + offlinePlayer.getName() + ChatColor.GRAY + "...");

                        sender.sendMessage("§8§m-----------------------------");

                        if(databaseManager.isPlayerMuted(offlinePlayer)) {
                            if(!databaseManager.getMuteLength(offlinePlayer).equalsIgnoreCase("forever")) {
                                sender.sendMessage(prefix + ChatColor.GRAY + "Muted: " + ChatColor.RED + "true " +
                                        ChatColor.GRAY + "(Issued by " + ChatColor.RED + "§n" + databaseManager.getMuteOperator(offlinePlayer) +
                                        ChatColor.GRAY + ")");
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Reason:" + ChatColor.RED +
                                        databaseManager.getMuteReason(offlinePlayer));
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Muted until: " + ChatColor.RED +
                                        dateManager.translateMillisToDate(Long.parseLong(databaseManager.getMutedUntil(offlinePlayer.getName()))));
                            } else {
                                sender.sendMessage(prefix + ChatColor.GRAY + "Muted: " + ChatColor.RED + "true " +
                                        ChatColor.GRAY + "(Issued by " + ChatColor.RED + "§n" + databaseManager.getMuteOperator(offlinePlayer) +
                                        ChatColor.GRAY + ")");
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Reason:" + ChatColor.RED +
                                        databaseManager.getMuteReason(offlinePlayer));
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Muted until: " + ChatColor.RED +
                                        "forever");
                            }
                        } else {
                            sender.sendMessage(prefix + ChatColor.GRAY + "Muted: " + ChatColor.RED + "false");
                        }

                        sender.sendMessage("");

                        if(databaseManager.isPlayerBanned(offlinePlayer)) {
                            if(!databaseManager.getBanLength(offlinePlayer).equalsIgnoreCase("forever")) {
                                sender.sendMessage(prefix + ChatColor.GRAY + "Banned: " + ChatColor.RED + "true " +
                                        ChatColor.GRAY + "(Issued by " + ChatColor.RED + "§n" + databaseManager.getBanOperator(offlinePlayer) +
                                        ChatColor.GRAY + ")");
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Reason:" + ChatColor.RED +
                                        databaseManager.getBanReason(offlinePlayer));
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Banned until: " + ChatColor.RED +
                                        dateManager.translateMillisToDate(Long.parseLong(databaseManager.getBannedUntil(offlinePlayer.getName()))));
                            } else {
                                sender.sendMessage(prefix + ChatColor.GRAY + "Banned: " + ChatColor.RED + "true " +
                                        ChatColor.GRAY + "(Issued by " + ChatColor.RED + "§n" + databaseManager.getBanOperator(offlinePlayer) +
                                        ChatColor.GRAY + ")");
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Reason:" + ChatColor.RED +
                                        databaseManager.getBanReason(offlinePlayer));
                                sender.sendMessage(prefix + ChatColor.GRAY + "  → Banned until: " + ChatColor.RED +
                                        "forever");
                            }
                        } else {
                            sender.sendMessage(prefix + ChatColor.GRAY + "Banned: " + ChatColor.RED + "false");
                        }
                        sender.sendMessage("§8§m-----------------------------");
                    }
                });
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /check (player)");
            }
        }
        return false;
    }
}
