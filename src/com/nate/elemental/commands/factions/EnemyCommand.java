package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.utils.storage.h2.FactionUtils;

public class EnemyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /f enemy <faction>");
            return true;
        }

        String factionName = player.getName();
        String enemyFaction = args[1];

        if (FactionUtils.factionExists(enemyFaction)) {
            FactionUtils.addEnemy(factionName, enemyFaction);
            player.sendMessage(ChatColor.GREEN + "You have declared " + enemyFaction + " as an enemy.");

            String broadcastMessage = ChatColor.BOLD + "" + ChatColor.DARK_RED + factionName + " has enemied " + enemyFaction + "!";
            Bukkit.broadcastMessage(broadcastMessage);
        } else {
            player.sendMessage(ChatColor.RED + "The specified faction does not exist.");
        }

        return true;
    }
}
