package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FactionUtils {

	 public static void changeFactionDescription(String factionName, String newDescription) {
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("UPDATE factions SET description = ? WHERE name = ?")) {
	            statement.setString(1, newDescription);
	            statement.setString(2, factionName);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
