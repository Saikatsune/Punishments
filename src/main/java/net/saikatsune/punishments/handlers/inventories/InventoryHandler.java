package net.saikatsune.punishments.handlers.inventories;

import net.saikatsune.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryHandler {

    private final Punishments plugin = Punishments.getInstance();

    private void fillEmptySlots(Inventory inventory) {
        for(int slot = 0; slot < inventory.getSize(); slot++) {
            if(inventory.getItem(slot) == null) {
                inventory.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE));
            }
        }
    }

    public void openBanInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*2, ChatColor.RED + "Select a ban length.");

        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "PERMANENT")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 YEAR")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "6 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "3 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "2 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 MONTH")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "14 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "7 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "3 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 DAY")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "12 HOURS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "6 HOURS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 HOUR")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "30 MINUTES")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 MINUTE")
                .build());

        player.openInventory(inventory);
    }

    public void openMuteInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*2, ChatColor.RED + "Select a mute length.");

        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "PERMANENT")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 YEAR")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "6 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "3 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "2 MONTHS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 MONTH")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "14 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "7 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "3 DAYS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 DAY")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "12 HOURS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "6 HOURS")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 HOUR")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "30 MINUTES")
                .build());
        inventory.addItem(new ItemHandler(Material.PAPER).setDisplayName(ChatColor.RED + "1 MINUTE")
                .build());

        player.openInventory(inventory);
    }

    public void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*4, ChatColor.RED + "Punishment Settings");

        ItemStack inkSackOn = new ItemStack(Material.INK_SACK, 1, (byte) 10);
        ItemMeta inkSackOnItemMeta = inkSackOn.getItemMeta();
        inkSackOnItemMeta.setDisplayName(ChatColor.GREEN + "ON");
        inkSackOn.setItemMeta(inkSackOnItemMeta);

        ItemStack inkSackOff = new ItemStack(Material.INK_SACK, 1, (byte) 8);
        ItemMeta inkSackOffItemMeta = inkSackOff.getItemMeta();
        inkSackOffItemMeta.setDisplayName(ChatColor.RED + "OFF");
        inkSackOff.setItemMeta(inkSackOffItemMeta);

        inventory.setItem(12, new ItemHandler(Material.BEACON).setDisplayName(ChatColor.RED + "Block VPN & Proxy servers (SOON)")
                .build());

        /*
        if(plugin.isVpnBlock()) {
            inventory.setItem(21, inkSackOn);
        } else {
            inventory.setItem(21, inkSackOff);
        }
         */

        inventory.setItem(21, inkSackOff);

        inventory.setItem(14, new ItemHandler(Material.REDSTONE_COMPARATOR).setDisplayName(ChatColor.RED + "Block IP's of banned accounts")
                .build());

        if(plugin.isIpBlock()) {
            inventory.setItem(23, inkSackOn);
        } else {
            inventory.setItem(23, inkSackOff);
        }

        this.fillEmptySlots(inventory);

        player.openInventory(inventory);
    }

}
