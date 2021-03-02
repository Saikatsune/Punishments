package net.saikatsune.punishments.listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.manager.DatabaseManager;
import net.saikatsune.punishments.manager.DateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class ConnectionListener implements Listener {

    private final Punishments plugin = Punishments.getInstance();
    private final DatabaseManager databaseManager = plugin.getDatabaseManager();
    private final DateManager dateManager = plugin.getDateManager();

    private final String appealWebsite = plugin.getAppealWebsite();

    private final String prefix = plugin.getPrefix();

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        databaseManager.cachePlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!databaseManager.getIPOfCachedPlayer(player.getName()).
                        equals(player.getAddress().getAddress().getHostAddress())) {
                    databaseManager.updateCachedIP(player);
                }
            }
        }.runTaskLater(plugin,10L);

        if(databaseManager.isIPAddressBanned(player)) {
            if(plugin.isIpBlock()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.kickPlayer(ChatColor.RED + "An account with this IP address has already " +
                                "been banned. If you think this is an error, contact a staff member!");
                    }
                }.runTaskLater(plugin, 10L);
            } else {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(allPlayers.hasPermission("punishments.staff")) {
                        TextComponent textComponent = new TextComponent();

                        textComponent.setText(prefix + ChatColor.DARK_RED + player.getName() + ChatColor.GRAY +
                                " is ban-evading. " + ChatColor.GREEN + "§l[Click Here]");

                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.GREEN + "Click here to ban " + player.getName() + ".").create()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ban " + player.getName() + " Ban Evading"));

                        allPlayers.sendMessage("");
                        allPlayers.spigot().sendMessage(textComponent);
                        allPlayers.sendMessage("");
                    }
                }
            }
        } else {
            /*
            if(plugin.getVpnConfig().getStringList("vpn-addresses").
                    contains(databaseManager.getIPOfCachedPlayer(player.getName()))) {
                if(plugin.isVpnBlock()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.kickPlayer(ChatColor.RED + "Using a VPN or Proxy is not allowed. " +
                                    "If you think this is an error, contact a staff member!");
                        }
                    }.runTaskLater(plugin, 10L);
                }
            }
             */
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        //Unbans the temporarily banned player.
        if(!databaseManager.getBanLength(player).equals("forever")) {
            if(System.currentTimeMillis() >= Long.parseLong(databaseManager.getBannedUntil(player.getName()))) {
                databaseManager.unbanPlayer(player);
                event.allow();
            }
        }

        if(databaseManager.isPlayerBanned(player)) {
            String reason = databaseManager.getBanReason(player);
            String operator = databaseManager.getBanOperator(player);

            if(databaseManager.getBanLength(player).equals("forever")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER ,ChatColor.RED + "You have been permanently banned. \n\n" +
                        ChatColor.RED + "Reason:" + reason + "\n\n" +
                        ChatColor.GRAY + "You can appeal at " + appealWebsite + ".");
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER ,ChatColor.RED + "You have been temporarily banned. \n\n" +
                        ChatColor.RED + "Reason:" + reason + "\n\n" +
                        ChatColor.RED + "Banned until: " + dateManager.translateMillisToDate(Long.parseLong(databaseManager.getBannedUntil(player.getName()))) + "\n\n" +
                        ChatColor.GRAY + "You can appeal at " + appealWebsite + ".");
            }
        }
    }

}
