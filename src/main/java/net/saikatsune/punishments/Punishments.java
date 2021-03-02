package net.saikatsune.punishments;

import net.saikatsune.punishments.commands.*;
import net.saikatsune.punishments.handlers.inventories.InventoryHandler;
import net.saikatsune.punishments.listener.ConnectionListener;
import net.saikatsune.punishments.listener.PlayerChatListener;
import net.saikatsune.punishments.manager.DatabaseManager;
import net.saikatsune.punishments.manager.DateManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;

public class Punishments extends JavaPlugin {

    private static Punishments instance;

    private FileConfiguration config, vpnConfig;

    private String prefix;
    private String appealWebsite;

    private String punishmentAnnouncements;

    private boolean ipBlock;
    private boolean vpnBlock;

    private DatabaseManager databaseManager;
    private DateManager dateManager;

    private InventoryHandler inventoryHandler;

    private HashMap<Player, String> playerBanningHash;
    private HashMap<Player, String> playerReasonHash;

    @Override
    public void onEnable() {
        instance = this;

        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        /*
        File vpnFile = new File(getDataFolder(), "vpn-list.yml");
        vpnConfig = YamlConfiguration.loadConfiguration(vpnFile);
         */

        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }

        /*
        if(!vpnFile.exists()) {
            saveResource("vpn-list.yml", false);
        }
         */

        prefix = config.getString("prefix").replace("&", "§").
                replace(">>", "»");
        appealWebsite = config.getString("website-link");

        punishmentAnnouncements = config.getString("punishments-announcements");

        ipBlock = getConfig().getBoolean("ip-block");
        vpnBlock = getConfig().getBoolean("vpn-block");

        inventoryHandler = new InventoryHandler();

        databaseManager = new DatabaseManager();
        dateManager = new DateManager();

        try {
            databaseManager.connectToDatabase();
        } catch (ClassNotFoundException ignored) {

        } catch (SQLException throwables) {
            System.out.println("[MySQL] Connection to database failed.");
        }

        try {
            databaseManager.createTables();
        } catch (SQLException throwables) {
            System.out.println("[MySQL] Table creation has failed.");
        }

        playerBanningHash = new HashMap<>();
        playerReasonHash = new HashMap<>();

        this.initialize(Bukkit.getPluginManager());
    }

    private void initialize(PluginManager pluginManager) {
        pluginManager.registerEvents(new ConnectionListener(), this);
        pluginManager.registerEvents(new PlayerChatListener(), this);

        pluginManager.registerEvents(new BanCommand(), this);
        pluginManager.registerEvents(new MuteCommand(), this);
        pluginManager.registerEvents(new GUICommand(), this);

        getCommand("kick").setExecutor(new KickCommand());
        getCommand("ban").setExecutor(new BanCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("mute").setExecutor(new MuteCommand());
        getCommand("unmute").setExecutor(new UnmuteCommand());
        getCommand("ipreport").setExecutor(new IPReportCommand());
        getCommand("check").setExecutor(new CheckCommand());
        getCommand("gui").setExecutor(new GUICommand());
    }

    @Override
    public void onDisable() {
        instance = null;

        try {
            databaseManager.disconnectFromDatabase();
        } catch (SQLException throwables) {
            System.out.println("[MySQL] Something went wrong with disconnecting from the database.");
        }
    }

    public static Punishments getInstance() {
        return instance;
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public String getPrefix() {
        return prefix;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DateManager getDateManager() {
        return dateManager;
    }

    public HashMap<Player, String> getPlayerBanningHash() {
        return playerBanningHash;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public HashMap<Player, String> getPlayerReasonHash() {
        return playerReasonHash;
    }

    public boolean isIpBlock() {
        return ipBlock;
    }

    public String getAppealWebsite() {
        return appealWebsite;
    }

    public String getPunishmentAnnouncements() {
        return punishmentAnnouncements;
    }

    public FileConfiguration getVpnConfig() {
        return vpnConfig;
    }

    public boolean isVpnBlock() {
        return vpnBlock;
    }

    public void setVpnBlock(boolean vpnBlock) {
        this.vpnBlock = vpnBlock;
    }

    public void setIpBlock(boolean ipBlock) {
        this.ipBlock = ipBlock;
    }
}
