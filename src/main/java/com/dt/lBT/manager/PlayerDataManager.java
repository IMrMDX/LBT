package com.dt.lBT.manager;

import com.dt.lBT.Main;
import com.dt.lBT.utils.Logger;

import java.sql.*;
import java.util.UUID;

public class PlayerDataManager {
    private final Main plugin;
    private Connection connection;

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/PlayerData.db");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "luckyBlockCount INTEGER, " +
                    "reached_limit BOOLEAN DEFAULT 0"+
                    ");";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void resetPlayerCount(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE player_data SET luckyBlockCount = 0 WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getReachedLimit(UUID uuid) {
        boolean reached_limit = false;
        try (PreparedStatement ps = connection.prepareStatement("SELECT reached_limit FROM player_data WHERE UUID = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reached_limit = rs.getBoolean("reached_limit");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reached_limit;
    }
    public void setReachedLimit(UUID uuid, boolean reachedLimit) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE player_data SET reached_limit = ? WHERE UUID = ?")) {
            ps.setBoolean(1, reachedLimit);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.ERROR, "&cTHERE'S A FATAL ERROR WHEN TRYING TO SET THE REACH LIMIT ON THE SQL");
            e.printStackTrace();
        }
    }


    public int getLuckyBlockCount(UUID uuid) {
        int luckyBlockCount = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT luckyBlockCount FROM player_data WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    luckyBlockCount = rs.getInt("luckyBlockCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return luckyBlockCount;
    }

//    public void incrementLuckyBlockCount(UUID uuid) {
//        int currentCount = getLuckyBlockCount(uuid);
//        setLuckyBlockCount(uuid, currentCount + 1);
//    }

    public void setLuckyBlockCount(UUID uuid, int count) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO player_data (uuid, luckyBlockCount) VALUES (?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, count);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetAllCounts() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE player_data SET luckyBlockCount = 0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
