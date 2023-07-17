package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

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
	 
	 //Ally Functions
	 
	   public static boolean areFactionsAllied(String faction1, String faction2) {
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM factions WHERE name = ? AND allies LIKE ?")) {
	            statement.setString(1, faction1);
	            statement.setString(2, "%" + faction2 + "%");
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
	 
	 public static String getFactionAllies(String factionName) {
		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement statement = connection.prepareStatement("SELECT allies FROM factions WHERE name = ?")) {
		        statement.setString(1, factionName);
		        ResultSet resultSet = statement.executeQuery();
		        if (resultSet.next()) {
		            String allies = resultSet.getString("allies");
		            resultSet.close();
		            return allies;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return "";
		}

	 public static void updateFactionAllies(String factionName, String allies) {
		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement statement = connection.prepareStatement("UPDATE factions SET allies = ? WHERE name = ?")) {
		        statement.setString(1, allies);
		        statement.setString(2, factionName);
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	 public static void addAlly(String factionName, String allyFaction) {
		    String currentAllies = getFactionAllies(factionName);
		    if (currentAllies == null) {
		        currentAllies = "";
		    } else if (!currentAllies.isEmpty()) {
		        currentAllies += ",";
		    }
		    currentAllies += allyFaction;
		    updateFactionAllies(factionName, currentAllies);
		}

	 
	 public int getFactionAlliesCount(String factionName) {
		    try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
		        ResultSet resultSet = statement.executeQuery("SELECT allies FROM factions WHERE name = '" + factionName + "'");
		        if (resultSet.next()) {
		            String allies = resultSet.getString("allies");
		            if (allies != null) {
		                return allies.split(",").length;
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return 0;
		}


	 //Ally Invite Functions
	 
	    public static void addInvite(String inviteKey, String faction1, String faction2) {
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("INSERT INTO allyinvites (invite_key, faction1, faction2) VALUES (?, ?, ?)")) {
	            statement.setString(1, inviteKey);
	            statement.setString(2, faction1);
	            statement.setString(3, faction2);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public static boolean hasPendingInvite(String inviteKey) {
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM allyinvites WHERE invite_key = ?")) {
	            statement.setString(1, inviteKey);
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

	    public static void removeInvite(String inviteKey) {
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("DELETE FROM allyinvites WHERE invite_key = ?")) {
	            statement.setString(1, inviteKey);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	 //Enemy Functions
	 
	 public static void addEnemy(String factionName, String enemyFaction) {
		    String currentEnemies = getFactionEnemies(factionName);
		    if (!currentEnemies.isEmpty()) {
		        currentEnemies += ",";
		    }
		    currentEnemies += enemyFaction;
		    updateFactionEnemies(factionName, currentEnemies);
		}

		public static String getFactionEnemies(String factionName) {
		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement statement = connection.prepareStatement("SELECT enemies FROM factions WHERE name = ?")) {
		        statement.setString(1, factionName);
		        ResultSet resultSet = statement.executeQuery();
		        if (resultSet.next()) {
		            String enemies = resultSet.getString("enemies");
		            resultSet.close();
		            return enemies;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return "";
		}

		public static void updateFactionEnemies(String factionName, String enemies) {
		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement statement = connection.prepareStatement("UPDATE factions SET enemies = ? WHERE name = ?")) {
		        statement.setString(1, enemies);
		        statement.setString(2, factionName);
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}


	 //Faction Functions
	 
	   
	    public static boolean factionExists(String name) {
	        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
	            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM factions WHERE name = '" + name + "'");
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
	    
	    public void storeRankPrefix(Player player, String selectedRank, String prefix) {
	        String factionName = UserTable.getUserFactionName(player.getName());
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement(
	                     "INSERT INTO rank_prefix (faction_name, rank, prefix) VALUES (?, ?, ?)")) {
	            statement.setString(1, factionName);
	            statement.setString(2, selectedRank);
	            statement.setString(3, prefix);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public String getRankPrefix(String factionName, String rank) {
	        String prefix = null;
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement("SELECT prefix FROM rank_prefix WHERE faction_name = ? AND rank = ?")) {
	            statement.setString(1, factionName);
	            statement.setString(2, rank);

	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                prefix = resultSet.getString("prefix");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return prefix;
	    }
	 
	}
