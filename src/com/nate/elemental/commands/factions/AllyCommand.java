package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;

public class AllyCommand implements CommandExecutor {
    private final Database database;

    public AllyCommand() {
        this.database = new Database();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /f ally <faction>");
            return true;
        }
        
        if (args.length > 2) {
        	if (args[1].contains("accept")) {
        	        String factionName = database.getUserFactionName(player.getName());
        	        String requestingFaction = args[2];

        	        if (!FactionUtils.factionExists(requestingFaction)) {
        	            player.sendMessage(ChatColor.RED + "The requesting faction does not exist.");
        	            return true;
        	        }

        	        String allianceRequestKey = requestingFaction + "_" + factionName;
        	        if (!FactionUtils.hasPendingInvite(allianceRequestKey)) {
        	            player.sendMessage(ChatColor.RED + "Your faction has not received an alliance request from the specified faction.");
        	            return true;
        	        }

        	        FactionUtils.addAlly(factionName, requestingFaction);
        	        FactionUtils.addAlly(requestingFaction, factionName);
        	        FactionUtils.removeInvite(allianceRequestKey);

        	        Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + factionName + " has allied " + requestingFaction + "!");

        	        return true;
        	    }
        	}

        String factionName = database.getUserFactionName(player.getName());
        String targetFaction = args[1];

        if (!FactionUtils.factionExists(targetFaction)) {
            player.sendMessage(ChatColor.RED + "The specified faction does not exist.");
            return true;
        }

        if (FactionUtils.areFactionsAllied(factionName, targetFaction)) {
            player.sendMessage(ChatColor.RED + "Your faction is already allied with the specified faction.");
            return true;
        }

        if (isProtectedFaction(targetFaction)) {
            player.sendMessage(ChatColor.RED + "Alliances with this faction are not allowed.");
            return true;
        }

        Player leader = Bukkit.getPlayer(database.getFactionLeader(targetFaction));
        if (leader == null || !leader.isOnline()) {
            player.sendMessage(ChatColor.RED + "The leader of the targeted faction needs to be online to accept the alliance request.");
            return true;
        }

        String allianceRequestKey = factionName + "_" + targetFaction;
        if (FactionUtils.hasPendingInvite(allianceRequestKey)) {
            player.sendMessage(ChatColor.RED + "An alliance request has already been sent to the targeted faction. Please wait for a response.");
            return true;
        }

        FactionUtils.addInvite(allianceRequestKey, factionName, targetFaction);
        leader.sendMessage(ChatColor.YELLOW + "The faction " + factionName + " wants to form an alliance with your faction.");
        leader.sendMessage(ChatColor.YELLOW + "To accept the alliance, use /f ally accept " + factionName);

        player.sendMessage(ChatColor.GREEN + "Your alliance request has been sent to the leader of " + targetFaction + ".");

        return true;
    }

    private boolean isProtectedFaction(String factionName) {
        return factionName.equalsIgnoreCase("wilderness") ||
                factionName.equalsIgnoreCase("safezone") ||
                factionName.equalsIgnoreCase("warzone");
    }
}
