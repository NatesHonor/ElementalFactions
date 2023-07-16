package com.nate.elemental.utils.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class GainPlayerPower {
	   private final Database database;
	    Factions plugin;
	    
	    public GainPlayerPower(Database database, Factions plugin) {
	        this.database = database;
	        this.plugin = plugin;
	    }
	 
	    Database databbase = new Database();
	public void enablePowerUpdates() {
	    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
	        for (Player player : Bukkit.getOnlinePlayers()) {
	            int power = database.getPlayerPower(player.getName());
	            if (power < 10) {
	                database.addPlayerPower(player.getName(), 1);
	            }
	        }
	    }, 12000, 12000);

	    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
	        for (Player player : Bukkit.getOnlinePlayers()) {
	            int power = database.getPlayerPower(player.getName());
	            if (power < 0) {
	            	database.addPlayerPower(player.getName(), 1);
	            }
	        }
	    }, 12000, 12000);
	}

	public void schedulePowerIncrease(String playerName) {
	    Bukkit.getScheduler().runTaskLater(plugin, () -> {
	        database.addPlayerPower(playerName, 1);
	        int newPower = database.getPlayerPower(playerName);
	        Player player = Bukkit.getPlayer(playerName);
	        if (player != null) {
	            player.sendMessage("Your power increased to " + newPower + "/10");
	        }
	        if (newPower < 10) {
	            schedulePowerIncrease(playerName);
	        }
	    }, 12000);
	}

	
}
