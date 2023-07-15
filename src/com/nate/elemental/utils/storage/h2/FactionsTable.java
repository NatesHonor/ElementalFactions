package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FactionsTable {
    public static boolean factionExists(String name) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM factions WHERE name = ?")) {
            statement.setString(1, name);
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

    public static void createTables() {
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS `user` ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255),"
                    + "chunks INT,"
                    + "power INT,"
                    + "faction_name VARCHAR(255),"
                    + "rank VARCHAR(255)"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS factions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255),"
                    + "description VARCHAR(255),"
                    + "chunks INT,"
                    + "owner VARCHAR(255),"
                    + "tag VARCHAR(255),"
                    + "invite_only INT,"
                    + "land INT,"
                    + "power INT,"
                    + "max_power INT,"
                    + "land_value INT,"
                    + "balance DOUBLE,"
                    + "spawners INT,"
                    + "allies_count INT,"
                    + "online_members_count INT,"
                    + "total_members_count INT,"
                    + "members VARCHAR(255)"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS claimed_chunks ("
                    + "faction_name VARCHAR(255),"
                    + "chunk_key VARCHAR(255)"
                    + ")");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
