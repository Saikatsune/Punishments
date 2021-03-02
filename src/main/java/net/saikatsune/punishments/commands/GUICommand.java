package net.saikatsune.punishments.commands;

import net.saikatsune.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUICommand implements CommandExecutor, Listener {

    private final Punishments plugin = Punishments.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("gui")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                plugin.getInventoryHandler().openSettingsInventory(player);
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if(event.getClickedInventory() != null) {
            if(event.getCurrentItem() != null) {
                if(event.getClickedInventory().getName().equalsIgnoreCase(ChatColor.RED + "Punishment Settings")) {
                    Player player = (Player) event.getWhoClicked();

                    if(event.getCurrentItem().getType() == Material.BEACON) {
                        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Block VPN & Proxy servers (SOON)")) {
                            if(event.getCurrentItem().getType() == Material.BEACON) {
                                event.setCancelled(true);

                                /*
                                if(plugin.isVpnBlock()) {
                                    plugin.setVpnBlock(false);

                                    plugin.getConfig().set("vpn-block", false);
                                } else {
                                    plugin.setVpnBlock(true);

                                    plugin.getConfig().set("vpn-block", true);
                                }

                                player.closeInventory();

                                plugin.getInventoryHandler().openSettingsInventory(player);
                                 */
                            }
                        }
                    } else if(event.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
                        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Block IP's of banned accounts")) {
                            if(event.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
                                event.setCancelled(true);

                                if(plugin.isIpBlock()) {
                                    plugin.setIpBlock(false);

                                    plugin.getConfig().set("ip-block", false);
                                } else {
                                    plugin.setIpBlock(true);

                                    plugin.getConfig().set("ip-block", true);
                                }

                                player.closeInventory();

                                plugin.getInventoryHandler().openSettingsInventory(player);
                            }
                        }
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
