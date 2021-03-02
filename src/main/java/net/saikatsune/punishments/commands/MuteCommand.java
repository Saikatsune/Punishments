package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.enums.PunishmentUnit;
import net.saikatsune.punishments.handlers.punishments.BanHandler;
import net.saikatsune.punishments.handlers.punishments.MuteHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("deprecation")
public class MuteCommand implements CommandExecutor, Listener {

    private final Punishments plugin = Punishments.getInstance();

    private final String prefix = plugin.getPrefix();
    private String reason = "";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("mute")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(args.length >= 2) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String playerToBan = Bukkit.getOfflinePlayer(args[0]).getName();

                        if(!plugin.getDatabaseManager().isPlayerMuted(Bukkit.getOfflinePlayer(playerToBan))) {
                            for (int i = 1; i < args.length; i++) {
                                reason = reason + " " + args[i];
                            }

                            plugin.getPlayerBanningHash().put(player, playerToBan);
                            plugin.getPlayerReasonHash().put(player, reason);

                            plugin.getInventoryHandler().openMuteInventory(player);

                            reason = "";
                        } else {
                            player.sendMessage(prefix + ChatColor.RED + playerToBan + " is already muted.");
                        }
                    });
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /mute (player) (reason)");
                }
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if(event.getClickedInventory() != null) {
            if(event.getCurrentItem() != null) {
                if(event.getClickedInventory().getName().contains("Select a mute length.")) {
                    Player player = (Player) event.getWhoClicked();

                    event.setCancelled(true);

                    if(event.getCurrentItem().getType() == Material.PAPER) {
                        String target = plugin.getPlayerBanningHash().get(player);
                        String reason = plugin.getPlayerReasonHash().get(player);

                        OfflinePlayer bukkitOfflinePlayer = Bukkit.getOfflinePlayer(target);

                        player.closeInventory();

                        switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
                            case "§cPERMANENT":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), -1, reason, PunishmentUnit.PERMANENT)
                                        .initialize();
                                break;
                            case "§c1 YEAR":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 12, reason, PunishmentUnit.MONTHS)
                                        .initialize();
                                break;
                            case "§c6 MONTHS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 6, reason, PunishmentUnit.MONTHS)
                                        .initialize();
                                break;
                            case "§c3 MONTHS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 3, reason, PunishmentUnit.MONTHS)
                                        .initialize();
                                break;
                            case "§c2 MONTHS":
                                new BanHandler(bukkitOfflinePlayer, player.getName(), 2, reason, PunishmentUnit.MONTHS)
                                        .initialize();
                                break;
                            case "§c1 MONTH":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 30, reason, PunishmentUnit.DAYS)
                                        .initialize();
                                break;
                            case "§c14 DAYS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 14, reason, PunishmentUnit.DAYS)
                                        .initialize();
                                break;
                            case "§c7 DAYS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 7, reason, PunishmentUnit.DAYS)
                                        .initialize();
                                break;
                            case "§c3 DAYS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 3, reason, PunishmentUnit.DAYS)
                                        .initialize();
                                break;
                            case "§c1 DAY":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 24, reason, PunishmentUnit.HOURS)
                                        .initialize();
                                break;
                            case "§c12 HOURS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 12, reason, PunishmentUnit.HOURS)
                                        .initialize();
                                break;
                            case "§c6 HOURS":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 6, reason, PunishmentUnit.HOURS)
                                        .initialize();
                                break;
                            case "§c1 HOUR":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 60, reason, PunishmentUnit.MINUTES)
                                        .initialize();
                                break;
                            case "§c30 MINUTES":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 30, reason, PunishmentUnit.MINUTES)
                                        .initialize();
                                break;
                            case "§c1 MINUTE":
                                new MuteHandler(bukkitOfflinePlayer, player.getName(), 1, reason, PunishmentUnit.MINUTES)
                                        .initialize();
                                break;
                        }
                    }
                }
            }
        }
    }

}
