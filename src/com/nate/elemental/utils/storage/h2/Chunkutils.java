package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class Chunkutils {

    public void setAutoClaiming(Player player, boolean value) {
        String playerName = player.getName();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("UPDATE users SET auto_claiming = ? WHERE name = ?")) {
            statement.setBoolean(1, value);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFactionNameByChunk(String chunkKey) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT faction_name FROM claimed_chunks WHERE chunk_key = ?")) {
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

    public boolean isChunkClaimed(String chunkKey) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
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
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO claimed_chunks (faction_name, chunk_key) VALUES (?, ?)")) {
            statement.setString(1, factionName);
            statement.setString(2, chunkKey);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAutoClaiming(Player player) {
        String playerName = player.getName();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT auto_claiming FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean autoClaiming = resultSet.getBoolean("auto_claiming");
                resultSet.close();
                return autoClaiming;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAvailableChunksForFaction(String factionName) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT chunks FROM factions WHERE name = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int chunks = resultSet.getInt("chunks");
                resultSet.close();
                return chunks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateAvailableChunksForFaction(String factionName, int chunks) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("UPDATE factions SET chunks = ? WHERE name = ?")) {
            statement.setInt(1, chunks);
            statement.setString(2, factionName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getClaimedChunksForFaction(String factionName) {
        int claimedChunks = 0;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT COUNT(*) FROM chunks WHERE faction = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                claimedChunks = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claimedChunks;
    }

    public int getUserPower(String playerName) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT power FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int power = resultSet.getInt("power");
                resultSet.close();
                return power;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFactionChunks(String factionName) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT chunks FROM factions WHERE name = ?")) {
            statement.setString(1, factionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int chunks = resultSet.getInt("chunks");
                resultSet.close();
                return chunks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUserChunks(String playerName) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT chunks FROM users WHERE name = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int chunks = resultSet.getInt("chunks");
                resultSet.close();
                return chunks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateFactionPower(String factionName, int newPower) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("UPDATE factions SET power = ? WHERE name = ?")) {
            statement.setInt(1, newPower);
            statement.setString(2, factionName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFactionChunks(String factionName, int newChunks) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("UPDATE factions SET chunks = ? WHERE name = ?")) {
            statement.setInt(1, newChunks);
            statement.setString(2, factionName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}