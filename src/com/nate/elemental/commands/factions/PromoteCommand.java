package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.utils.storage.h2.Database;

public class PromoteCommand implements CommandExecutor {
    private final Database database;

    public PromoteCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        String factionName = database.getUserFactionName(playerName);
        if (factionName == null || database.getUserRank(playerName) == "member") {
            player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return true;
        }

        String targetPlayerName = args[1];

        String targetFactionName = database.getUserFactionName(targetPlayerName);
        if (targetFactionName == null || !targetFactionName.equals(factionName)) {
            player.sendMessage(ChatColor.RED + "The specified player is not a member of your faction.");
            return true;
        }

        String playerRank = database.getUserRank(playerName);
        String targetPlayerRank = database.getUserRank(targetPlayerName);

        if (playerRank.equals("founder")) {
        	if (targetPlayerRank.equals("coleader")) {
                player.sendMessage(ChatColor.RED + "You have already promoted this user faction leader and cannot be promoted.");
            } else if (targetPlayerRank.equals("moderator")) {
                database.setUserRank(targetPlayerName, "coleader");
                player.sendMessage(ChatColor.GREEN + targetPlayerName + " has been promoted to CoLeader.");
            } else if (targetPlayerRank.equals("trusted_member")) {
                    database.setUserRank(targetPlayerName, "moderator");
                    player.sendMessage(ChatColor.GREEN + targetPlayerName + " has been promoted to Moderator!.");
                }	else if (targetPlayerRank.equals("member")) {
                	database.setUserRank(targetPlayerName, "trusted_member");
                	player.sendMessage(ChatColor.GREEN + targetPlayerName + " has been promoted to Trusted Member!");
                }
        	
        }
 
        if (targetPlayerRank.equals("coleader")) {
            player.sendMessage(ChatColor.RED + "You have already promoted this user faction leader and cannot be promoted.");
        } else if (playerRank.equals("coleader")) {
            player.sendMessage(ChatColor.RED + "As a coleader, you can only promote players to moderator rank.");
        } else if (playerRank.equals("moderator")) {
            if (targetPlayerRank.equals("trusted_member")) {
                database.setUserRank(targetPlayerName, "moderator");
                player.sendMessage(ChatColor.GREEN + targetPlayerName + " has been promoted to moderator.");
            } else {
                player.sendMessage(ChatColor.RED + "You can only promote trusted members to moderator rank.");
            }
        } else if (playerRank.equals("trusted_member")) {
            if (targetPlayerRank.equals("member")) {
                database.setUserRank(targetPlayerName, "trusted_member");
                player.sendMessage(ChatColor.GREEN + targetPlayerName + " has been promoted to trusted member.");
            } else {
                player.sendMessage(ChatColor.RED + "You can only promote members to trusted member rank.");
            }
        }

        return true;
    }
}
