package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvitesTable {

    public void addInvite(String inviterName, String inviteeName, String factionName, long expiryTime) {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO invites (inviter_name, invitee_name, faction_name, expiry_time) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, inviterName);
            statement.setString(2, inviteeName);
            statement.setString(3, factionName);
            statement.setLong(4, expiryTime);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isInvitePending(String inviterName, String inviteeName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) AS count FROM invites WHERE inviter_name = ? AND invitee_name = ?")) {
            statement.setString(1, inviterName);
            statement.setString(2, inviteeName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
   
    public void removeInvite(String inviterName, String inviteeName) {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM invites WHERE inviter_name = ? AND invitee_name = ?")) {
            statement.setString(1, inviterName);
            statement.setString(2, inviteeName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	
}
