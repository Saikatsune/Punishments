package net.saikatsune.punishments.handlers.punishments;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.enums.PunishmentUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MuteHandler {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();

    private final OfflinePlayer target;
    private final String operator;
    private final int duration;
    private final String reason;
    private final PunishmentUnit unit;

    public MuteHandler(OfflinePlayer target, String operator, int duration, String reason, PunishmentUnit unit) {
        this.target = target;
        this.operator = operator;
        this.duration = duration;
        this.reason = reason;
        this.unit = unit;
    }

    public void initialize() {
        if(unit == PunishmentUnit.PERMANENT) {
            plugin.getDatabaseManager().
                    createPermanentMute(target.getName(), operator, reason);

            if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("public")) {
                Bukkit.broadcastMessage(prefix + ChatColor.RED + target.getName() + " has been muted for" + reason +
                        " for a period of forever.");
            } else if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("private")) {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(allPlayers.hasPermission("punishments.staff")) {
                        allPlayers.sendMessage(prefix + ChatColor.RED + target.getName() + " has been muted for" + reason +
                                " for a period of forever.");
                    }
                }

                if(target != null) {
                    Player player = Bukkit.getPlayer(target.getUniqueId());

                    player.sendMessage(prefix + ChatColor.RED + "You have been muted for" + reason +
                            " for a period of forever.");
                }
            }
        } else {
            plugin.getDatabaseManager().
                    createTemporaryMute(target.getName(), operator, reason, String.valueOf(duration), unit);

            if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("public")) {
                Bukkit.broadcastMessage(prefix + ChatColor.RED + target.getName() + " has been muted for" + reason +
                        " for a period of " + duration + " " + unit.toString().toLowerCase() + ".");
            } else if(plugin.getPunishmentAnnouncements().equalsIgnoreCase("private")) {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(allPlayers.hasPermission("punishments.staff")) {
                        allPlayers.sendMessage(prefix + ChatColor.RED + target.getName() + " has been muted for" + reason +
                                " for a period of " + duration + " " + unit.toString().toLowerCase() + ".");
                    }
                }

                if(target != null) {
                    Player player = Bukkit.getPlayer(target.getUniqueId());

                    player.sendMessage(prefix + ChatColor.RED + "You have been muted for" + reason +
                            " for a period of " + duration + " " + unit.toString().toLowerCase() + ".");
                }
            }
        }
    }

}
