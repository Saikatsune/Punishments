package net.saikatsune.punishments.handlers.punishments;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.enums.PunishmentUnit;
import net.saikatsune.punishments.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BanHandler {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();
    private final String appealWebsite = plugin.getAppealWebsite();

    private final DatabaseManager databaseManager = plugin.getDatabaseManager();

    private final OfflinePlayer target;
    private final String operator;
    private final int duration;
    private final String reason;
    private final PunishmentUnit unit;

    public BanHandler(OfflinePlayer target, String operator, int duration, String reason, PunishmentUnit unit) {
        this.target = target;
        this.operator = operator;
        this.duration = duration;
        this.reason = reason;
        this.unit = unit;
    }

    public void initialize() {
        if(unit == PunishmentUnit.PERMANENT) {
            if(target.isOnline()) {
                Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());

                onlineTarget.kickPlayer(ChatColor.RED + "You have been permanently banned. \n\n" +
                        ChatColor.RED + "Reason:" + reason + "\n\n" +
                        ChatColor.GRAY + "You can appeal at " + appealWebsite + ".");
            }

            plugin.getDatabaseManager().
                    createPermanentBan(target.getName(), operator, reason);

            if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("public")) {
                Bukkit.broadcastMessage(prefix + ChatColor.RED + target.getName() + " has been banned for" + reason +
                        " for a period of forever.");
            } else if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("private")) {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(allPlayers.hasPermission("punishments.staff")) {
                        allPlayers.sendMessage(prefix + ChatColor.RED + target.getName() + " has been banned for" + reason +
                                " for a period of forever.");
                    }
                }
            }
        } else {
            if(target.isOnline()) {
                Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());

                onlineTarget.kickPlayer(ChatColor.RED + "You have been temporarily banned for " + duration + " " +
                        unit.toString().toLowerCase() + ".\n\n" + ChatColor.RED + "Reason:" + reason + "\n\n"
                    + ChatColor.GRAY + "You can appeal at " + appealWebsite + ".");
            }

            plugin.getDatabaseManager().
                    createTemporaryBan(target.getName(), operator, reason, String.valueOf(duration), unit);

            if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("public")) {
                Bukkit.broadcastMessage(prefix + ChatColor.RED + target.getName() + " has been banned for" + reason +
                        " for a period of " + duration + " " + unit.toString().toLowerCase() + ".");
            } else if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("private")) {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(allPlayers.hasPermission("punishments.staff")) {
                        allPlayers.sendMessage(prefix + ChatColor.RED + target.getName() + " has been banned for" + reason +
                                " for a period of " + duration + " " + unit.toString().toLowerCase() + ".");
                    }
                }
            }
        }

        if(plugin.isIpBlock()) {
            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                if(databaseManager.isIPAddressBanned(allPlayers)) {
                    allPlayers.kickPlayer(ChatColor.RED + "An account with this IP address has been banned.");
                }
            }
        }

    }

}
