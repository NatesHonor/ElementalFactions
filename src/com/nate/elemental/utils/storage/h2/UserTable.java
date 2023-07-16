package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class UserTable {
    public static boolean playerExists(String playerName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM `user` WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                resultSet.close();
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void subtractPowerAndChunks(String playerName, int amount) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET power = power - ?, chunks = chunks - ? WHERE name = ?")) {
            statement.setInt(1, amount);
            statement.setInt(2, amount);
            statement.setString(3, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 

    public static void addPlayer(String playerName, String factionName, int power, int chunks) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `user` (name, faction_name, power, chunks) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, playerName);
            statement.setString(2, factionName);
            statement.setInt(3, power);
            statement.setInt(4, chunks);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getUserFactionName(String playerName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT faction_name FROM `user` WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String factionName = resultSet.getString("faction_name");
                resultSet.close();
                Bukkit.getLogger().info(playerName + " is in the faction: " + factionName);
                return factionName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUserFaction(String playerName, String factionName, String rank) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `user` SET faction_name = ?, rank = ? WHERE name = ?")) {
            statement.setString(1, factionName);
            statement.setString(2, rank);
            statement.setString(3, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
