package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class DisbandCommand implements CommandExecutor {
    private final Database database;
    @SuppressWarnings("unused")
	private Factions plugin;

    public DisbandCommand(Factions factions) {
        this.plugin = factions;
        this.database = new Database();
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

        if (factionName == null || factionName.equals("wilderness") || factionName.equals("warzone") || factionName.equals("safezone")) {
            player.sendMessage(ChatColor.RED + "You are not a leader of any faction that can be disbanded.");
            return true;
        } 

        String factionLeader = database.getFactionLeader(factionName);
        if (factionLeader == null || !factionLeader.equalsIgnoreCase(playerName)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to disband your faction.");
            return true;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
            database.disbandFaction(factionName);
            database.updateusersFactionNoLeader(playerName, factionName);

            String message = ChatColor.RED + "The faction " + ChatColor.YELLOW + factionName + ChatColor.RED + " has been disbanded.";
            Bukkit.broadcastMessage(message);
            return true;
        }

        String confirmMessage = ChatColor.YELLOW + "Are you sure you want to disband your faction? This action cannot be undone.";
        String confirmCommand = ChatColor.GREEN + "/f disband confirm";
        player.sendMessage(confirmMessage);
        player.sendMessage(confirmCommand);
        return true;
    }

}
