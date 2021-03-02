package net.saikatsune.punishments.listener;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.manager.DatabaseManager;
import net.saikatsune.punishments.manager.DateManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final Punishments plugin = Punishments.getInstance();
    private final DatabaseManager databaseManager = plugin.getDatabaseManager();
    private final DateManager dateManager = plugin.getDateManager();

    private final String prefix = plugin.getPrefix();

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        String reason = databaseManager.getMuteReason(player);

        if(databaseManager.isPlayerMuted(player)) {
            if(!databaseManager.getMuteLength(player).equals("forever")) {
                if(System.currentTimeMillis() >= Long.parseLong(databaseManager.getMutedUntil(player.getName()))) {
                    databaseManager.unmutePlayer(player);
                } else {
                    event.setCancelled(true);

                    player.sendMessage(prefix + ChatColor.RED + "You are currently muted for" + reason + " until " +
                            dateManager.translateMillisToDate(Long.parseLong(databaseManager.getMutedUntil(player.getName())))
                            + ".");
                }
            } else {
                event.setCancelled(true);

                player.sendMessage(prefix + ChatColor.RED + "You are currently muted for" + reason + " until forever.");
            }
        } else {
            if(databaseManager.isIPAddressMuted(player)) {
                if(plugin.isIpBlock()) {
                    event.setCancelled(true);

                    player.sendMessage(prefix + ChatColor.RED + "An account with this IP address has already " +
                            "been muted. If you think this is an error, contact a staff member!");
                }
            }
        }
    }

}
