package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nate.elemental.Factions;

public class Database {
	private static final String DB_URL = Factions.getConnectionURL();
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public boolean tableExists(String tableName) {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

 
    public void createFaction(String name, String owner, String description, int power, int chunks) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO factions (name, owner, description, power, chunks) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, owner);
            statement.setString(3, "A faction!");
            statement.setInt(4, power);
            statement.setInt(5, chunks);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateusersFaction(owner, name, "founder");
    }
    

    public int getPlayerPower(String playerName) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT power FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("power");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void subtractPlayerPower(String playerName, int amount) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET power = power - ? WHERE name = ?")) {
            statement.setInt(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerPower(String playerName, int amount) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET power = power + ? WHERE name = ?")) {
            statement.setInt(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getInviterFactionName(String inviteeName, String inviterName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT faction_name FROM invites WHERE invitee_name = ? AND inviter_name = ?")) {
            statement.setString(1, inviteeName);
            statement.setString(2, inviterName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("faction_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void acceptFactionInvitation(String inviteeName, String inviterName) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement updateUserStatement = connection.prepareStatement(
                    "UPDATE users SET faction_name = ?, rank = ? WHERE name = ?")) {
                String factionName = getInviterFactionName(inviteeName, inviterName);
                updateUserStatement.setString(1, factionName);
                updateUserStatement.setString(2, "member");
                updateUserStatement.setString(3, inviteeName);
                updateUserStatement.executeUpdate();
            }

            try (PreparedStatement removeInviteStatement = connection.prepareStatement(
                    "DELETE FROM invites WHERE invitee_name = ? AND inviter_name = ?")) {
                removeInviteStatement.setString(1, inviteeName);
                removeInviteStatement.setString(2, inviterName);
                removeInviteStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getUserFactionName(String playerName) {
        String factionName = "";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT faction_name FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                factionName = resultSet.getString("faction_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionName;
    }
    
    public String getFactionNameByChunk(String chunkKey) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT faction_name FROM claimed_chunks WHERE chunk_key = ?")) {
            statement.setString(1, chunkKey);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("faction_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserRank(String playerName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT rank FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("rank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFactionLeader(String playerName, String factionName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT owner FROM factions WHERE name = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String owner = resultSet.getString("owner");
                return owner.equals(playerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setUserRank(String playerName, String rank) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET rank = ? WHERE name = ?")) {
            statement.setString(1, rank);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
    public int getusersPower(String playerName) {
        int power = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT power FROM `users` WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                power = resultSet.getInt("power");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return power;
    }
    
    public void updateusersFaction(String playerName, String factionName, String rank) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "UPDATE `users` SET faction_name = ?, rank = ? WHERE name = ?")) {
            statement.setString(1, factionName);
            statement.setString(2, rank);
            statement.setString(3, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateusersFactionNoLeader(String playerName, String factionName) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE `users` SET faction_name = CONCAT(faction_name, ?) WHERE name = ?")) {
            statement.setString(1, ", " + playerName);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(String playerName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM `users` WHERE name = '" + playerName + "'");
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                resultSet.close();
                statement.close();
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayer(String playerName, String factionName, int power, int chunks) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            String sql = "INSERT INTO `users` (name, faction_name, power, chunks) VALUES ('" + playerName + "', '" + factionName + "', " + power + ", " + chunks + ")";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addusersToDefaultFaction(String playerName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `users` (chunks, power, faction_name, rank) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, 0);
            statement.setInt(2, 10);
            statement.setString(3, "wilderness");
            statement.setString(4, "");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public int calculateFactionPower(String playerName) {
        int factionPower = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT SUM(power) AS total_power FROM `users` WHERE faction_name = (SELECT faction_name FROM `users` WHERE id = (SELECT id FROM `users` WHERE name = ?))")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                factionPower = resultSet.getInt("total_power");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionPower;
    }

    
    
    public List<String> getFactions() {
        List<String> factions = new ArrayList<>();
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT name FROM factions");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                factions.add(name);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factions;
    }

    public String getFactionDescription(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT description FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                String description = resultSet.getString("description");
                resultSet.close();
                statement.close();
                return description;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFactionInviteOnly(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT invite_only FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int inviteOnly = resultSet.getInt("invite_only");
                resultSet.close();
                statement.close();
                return inviteOnly == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void setusersFactionName(String playerName, String factionName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "UPDATE `users` SET faction_name = ? WHERE name = ?")) {
            statement.setString(1, factionName);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int getFactionLand(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT land FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int land = resultSet.getInt("land");
                resultSet.close();
                statement.close();
                return land;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFactionPower(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT power FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int power = resultSet.getInt("power");
                resultSet.close();
                statement.close();
                return power;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getMaxFactionPower(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT max_power FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int maxPower = resultSet.getInt("max_power");
                resultSet.close();
                statement.close();
                return maxPower;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFactionLandValue(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT land_value FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int landValue = resultSet.getInt("land_value");
                resultSet.close();
                statement.close();
                return landValue;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getFactionBalance(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT balance FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                resultSet.close();
                statement.close();
                return balance;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFactionSpawners(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT spawners FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int spawners = resultSet.getInt("spawners");
                resultSet.close();
                statement.close();
                return spawners;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isChunkClaimed(String chunkKey) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) AS count FROM claimed_chunks WHERE chunk_key = ?")) {
            statement.setString(1, chunkKey);
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

    public void claimChunk(String factionName, String chunkKey) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO claimed_chunks (faction_name, chunk_key) VALUES (?, ?)")) {
            statement.setString(1, factionName);
            statement.setString(2, chunkKey);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int getFactionOnlineMembersCount(String factionName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT online_members_count FROM factions WHERE name = '" + factionName + "'");
            if (resultSet.next()) {
                int onlineMembersCount = resultSet.getInt("online_members_count");
                resultSet.close();
                statement.close();
                return onlineMembersCount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getUsersInFactionCount(String factionName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) AS count FROM users WHERE faction_name = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public String getFactionLeader(String factionName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT owner FROM factions WHERE name = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String leader = resultSet.getString("owner");
                resultSet.close();
                return leader;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disbandFaction(String factionName) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM factions WHERE name = ?")) {
            statement.setString(1, factionName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public List<String> getusersData() {
        List<String> usersData = new ArrayList<>();
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `users`");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    row.append(columnName).append(": ").append(columnValue).append(" | ");
                }
                usersData.add(row.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersData;
    }


    
}