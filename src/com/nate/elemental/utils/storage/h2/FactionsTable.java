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
            statement.execute("CREATE TABLE IF NOT EXISTS users ("
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
                    + "members VARCHAR(255),"
                    + "allies VARCHAR(500),"
                    + "truces VARCHAR(500),"
                    + "enemies VARCHAR(500)"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS rank_prefix ("
                    + "faction_name VARCHAR(255) NOT NULL,"
                    + "rank VARCHAR(255) NOT NULL,"
                    + "prefix VARCHAR(255) NOT NULL,"
                    + "PRIMARY KEY (faction_name, rank)"
                    + ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS claimed_chunks ("
                    + "faction_name VARCHAR(255),"
                    + "chunk_key VARCHAR(255)"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS invites ("
                    + "inviter_name VARCHAR(255),"
                    + "invitee_name VARCHAR(255),"
                    + "faction_name VARCHAR(255),"
                    + "expiry_time BIGINT"
                    + ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS allyinvites ("
                    + "invite_key VARCHAR(255) PRIMARY KEY,"
                    + "faction1 VARCHAR(255),"
                    + "faction2 VARCHAR(255)"
                    + ")");

            if (!factionExists("wilderness")) {
                statement.execute("INSERT INTO factions (name, description, chunks, owner, tag, invite_only, land, power, max_power, land_value, balance, spawners, allies_count, online_members_count, total_members_count, members, allies, truces, enemies) VALUES ('wilderness', 'PvP is Enabled and it''s cold out here.', 0, '', '', 0, 0, 0, 0, 0, 0.0, 0, 0, 0, 0, '', '', '', '')");
            }

            if (!factionExists("warzone")) {
                statement.execute("INSERT INTO factions (name, description, chunks, owner, tag, invite_only, land, power, max_power, land_value, balance, spawners, allies_count, online_members_count, total_members_count, members, allies, truces, enemies) VALUES ('warzone', 'PvP Enabled', 0, '', '', 0, 0, 0, 0, 0, 0.0, 0, 0, 0, 0, '', '', '', '')");
            }

            if (!factionExists("safezone")) {
                statement.execute("INSERT INTO factions (name, description, chunks, owner, tag, invite_only, land, power, max_power, land_value, balance, spawners, allies_count, online_members_count, total_members_count, members, allies, truces, enemies) VALUES ('safezone', 'PvP is Disabled', 0, '', '', 0, 0, 0, 0, 0, 0.0, 0, 0, 0, 0, '', '', '', '')");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
