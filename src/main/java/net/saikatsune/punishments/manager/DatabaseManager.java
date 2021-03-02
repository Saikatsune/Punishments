package net.saikatsune.punishments.manager;

import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.enums.PunishmentUnit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;

@SuppressWarnings("deprecation")
public class DatabaseManager {
    private final Punishments plugin = Punishments.getInstance();

    private Connection connection;

    private final String host = plugin.getConfig().getString("mysql.host");
    private final String database = plugin.getConfig().getString("mysql.database");
    private final String username = plugin.getConfig().getString("mysql.username");
    private final String password = plugin.getConfig().getString("mysql.password");

    private final int port = plugin.getConfig().getInt("mysql.port");

    public void connectToDatabase() throws ClassNotFoundException, SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" +
                    this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
        }
    }

    public void disconnectFromDatabase() throws SQLException {
        if (!connection.isClosed()) connection.close();
    }

    public void checkConnection() {
        try {
            if (this.connection == null || !this.connection.isValid(10) || this.connection.isClosed()) connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws SQLException {
        this.checkConnection();

        Statement statement = connection.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS PLAYER_CACHE(USERNAME VARCHAR(100), UUID VARCHAR(100), " +
                "IP_ADDRESS VARCHAR(100))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS BANS(USERNAME VARCHAR(100), UUID VARCHAR(100), " +
                "BANNED_BY VARCHAR(100), REASON VARCHAR(100), LENGTH VARCHAR(100), UNIT VARCHAR(100), " +
                "BANNED_UNTIL VARCHAR(100), DATE VARCHAR(100), IP_ADDRESS VARCHAR(100))");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS MUTES(USERNAME VARCHAR(100), UUID VARCHAR(100), " +
                "MUTED_BY VARCHAR(100), REASON VARCHAR(100), LENGTH VARCHAR(100), UNIT VARCHAR(100), MUTED_UNTIL " +
                "VARCHAR(100), DATE VARCHAR(100), IP_ADDRESS VARCHAR(100))");
    }

    public boolean isPlayerCached(OfflinePlayer player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PLAYER_CACHE WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void cachePlayer(Player player) {
        this.checkConnection();

        try {
            if (!this.isPlayerCached(player)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO PLAYER_CACHE(USERNAME, UUID, IP_ADDRESS) VALUE (?,?,?)");
                statement.setString(1, player.getName());
                statement.setString(2, player.getUniqueId().toString());
                statement.setString(3, player.getAddress().getAddress().getHostAddress());

                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createPermanentBan(String playerName, String operator, String reason) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO BANS(USERNAME," +
                    " UUID, BANNED_BY, REASON, LENGTH, UNIT, BANNED_UNTIL, DATE, IP_ADDRESS) VALUE (?,?,?,?,?,?,?,?,?)");
            statement.setString(1, playerName);
            statement.setString(2, String.valueOf(Bukkit.getOfflinePlayer(playerName).getUniqueId()));
            statement.setString(3, operator);
            statement.setString(4, reason);
            statement.setString(5, "forever");
            statement.setString(6, "/--/");
            statement.setString(7, "/--/");
            statement.setString(8, String.valueOf(System.currentTimeMillis()));

            Player player = Bukkit.getPlayer(playerName);

            if(player == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                if(this.isPlayerCached(offlinePlayer)) {
                    statement.setString(9, this.getIPOfCachedPlayer(offlinePlayer.getName()));
                } else {
                    statement.setString(9, "N/A - NEVER ONLINE");
                }
            } else {
                statement.setString(9, player.getAddress().getAddress().getHostAddress());
            }

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public void createTemporaryBan(String playerName, String operator, String reason, String length, PunishmentUnit unit) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO BANS(USERNAME," +
                    " UUID, BANNED_BY, REASON, LENGTH, UNIT, BANNED_UNTIL, DATE, IP_ADDRESS) VALUE (?,?,?,?,?,?,?,?,?)");
            statement.setString(1, playerName);
            statement.setString(2, String.valueOf(Bukkit.getOfflinePlayer(playerName).getUniqueId()));
            statement.setString(3, operator);
            statement.setString(4, reason);
            statement.setString(5, length);
            statement.setString(6, unit.toString());

            if(unit == PunishmentUnit.MINUTES) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 60000L)));
            } else if(unit == PunishmentUnit.HOURS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 3600000L)));
            } else if(unit == PunishmentUnit.DAYS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 86400000L)));
            } else if(unit == PunishmentUnit.MONTHS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 657436500L * 4)));
            }

            statement.setString(8, String.valueOf(System.currentTimeMillis()));

            Player player = Bukkit.getPlayer(playerName);

            if(player == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                if(this.isPlayerCached(offlinePlayer)) {
                    statement.setString(9, this.getIPOfCachedPlayer(offlinePlayer.getName()));
                } else {
                    statement.setString(9, "N/A - NEVER ONLINE");
                }
            } else {
                statement.setString(9, player.getAddress().getAddress().getHostAddress());
            }

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public boolean isPlayerBanned(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
            statement.setString(1, offlinePlayer.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String getIPOfCachedPlayer(String player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PLAYER_CACHE WHERE UUID=?");
            statement.setString(1, Bukkit.getOfflinePlayer(player).getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("IP_ADDRESS");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "N/A";
    }

    public String getBannedUntil(String player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
            statement.setString(1, Bukkit.getOfflinePlayer(player).getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("BANNED_UNTIL");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return String.valueOf(0);
    }

    public String getBannedDate(String player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
            statement.setString(1, Bukkit.getOfflinePlayer(player).getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("DATE");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "N/A";
    }

    public String getBanLength(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerBanned(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("LENGTH");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public String getBanReason(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerBanned(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("REASON");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public String getBanOperator(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerBanned(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("BANNED_BY");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public String getBanUnit(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerBanned(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("UNIT");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public void unbanPlayer(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM BANS WHERE UUID=?");

            statement.setString(1, offlinePlayer.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public boolean isPlayerMuted(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE UUID=?");
            statement.setString(1, offlinePlayer.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void createPermanentMute(String playerName, String operator, String reason) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO MUTES(USERNAME," +
                    " UUID, MUTED_BY, REASON, LENGTH, UNIT, MUTED_UNTIL, DATE, IP_ADDRESS) VALUE (?,?,?,?,?,?,?,?,?)");
            statement.setString(1, playerName);
            statement.setString(2, String.valueOf(Bukkit.getOfflinePlayer(playerName).getUniqueId()));
            statement.setString(3, operator);
            statement.setString(4, reason);
            statement.setString(5, "forever");
            statement.setString(6, "/--/");
            statement.setString(7, "/--/");
            statement.setString(8, String.valueOf(System.currentTimeMillis()));

            Player player = Bukkit.getPlayer(playerName);

            if(player == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                if(this.isPlayerCached(offlinePlayer)) {
                    statement.setString(9, this.getIPOfCachedPlayer(offlinePlayer.getName()));
                } else {
                    statement.setString(9, "N/A - NEVER ONLINE");
                }
            } else {
                statement.setString(9, player.getAddress().getAddress().getHostAddress());
            }

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public void createTemporaryMute(String playerName, String operator, String reason, String length, PunishmentUnit unit) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO MUTES(USERNAME," +
                    " UUID, MUTED_BY, REASON, LENGTH, UNIT, MUTED_UNTIL, DATE, IP_ADDRESS) VALUE (?,?,?,?,?,?,?,?,?)");
            statement.setString(1, playerName);
            statement.setString(2, String.valueOf(Bukkit.getOfflinePlayer(playerName).getUniqueId()));
            statement.setString(3, operator);
            statement.setString(4, reason);
            statement.setString(5, length);
            statement.setString(6, unit.toString());

            if(unit == PunishmentUnit.MINUTES) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 60000L)));
            } else if(unit == PunishmentUnit.HOURS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 3600000L)));
            } else if(unit == PunishmentUnit.DAYS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 86400000L)));
            } else if(unit == PunishmentUnit.MONTHS) {
                statement.setString(7, String.valueOf(System.currentTimeMillis() + (Long.parseLong(length) * 657436500L * 4)));
            }

            statement.setString(8, String.valueOf(System.currentTimeMillis()));

            Player player = Bukkit.getPlayer(playerName);

            if(player == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                if(this.isPlayerCached(offlinePlayer)) {
                    statement.setString(9, this.getIPOfCachedPlayer(offlinePlayer.getName()));
                } else {
                    statement.setString(9, "N/A - NEVER ONLINE");
                }
            } else {
                statement.setString(9, player.getAddress().getAddress().getHostAddress());
            }

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public String getMutedUntil(String player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE UUID=?");
            statement.setString(1, Bukkit.getOfflinePlayer(player).getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("MUTED_UNTIL");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return String.valueOf(0);
    }

    public String getMuteReason(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerMuted(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("REASON");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public void unmutePlayer(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM MUTES WHERE UUID=?");

            statement.setString(1, offlinePlayer.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.getStackTrace();
        }
    }

    public String getMuteLength(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerMuted(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("LENGTH");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public String getMuteOperator(OfflinePlayer offlinePlayer) {
        this.checkConnection();

        if (this.isPlayerMuted(offlinePlayer)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE UUID=?");
                statement.setString(1, offlinePlayer.getUniqueId().toString());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("MUTED_BY");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "N/A";
    }

    public boolean isIPAddressMuted(Player player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MUTES WHERE IP_ADDRESS=?");
            statement.setString(1, player.getAddress().getAddress().getHostAddress());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isIPAddressBanned(Player player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BANS WHERE IP_ADDRESS=?");
            statement.setString(1, player.getAddress().getAddress().getHostAddress());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void updateCachedIP(Player player) {
        this.checkConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE PLAYER_CACHE SET IP_ADDRESS=? WHERE UUID=?");

            statement.setString(1, player.getAddress().getAddress().getHostAddress());
            statement.setString(2, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
