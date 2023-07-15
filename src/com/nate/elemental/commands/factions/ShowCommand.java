package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class ShowCommand implements CommandExecutor {
    Database database = new Database();

    @SuppressWarnings("unused")
    private Factions plugin;

    public ShowCommand(Factions factions) {
        this.plugin = factions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        String factionName = database.getUserFactionName(player.getName());

        if (factionName.equals("wilderness") || factionName.equals("warzone") || factionName.equals("safezone")) {
            player.sendMessage(ChatColor.RED + "You are not currently in a faction.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Faction Details: " + factionName);
        player.sendMessage(ChatColor.YELLOW + "User Data:");


        String description = database.getFactionDescription(factionName);
        boolean isInviteOnly = database.isFactionInviteOnly(factionName);
        int land = database.getFactionLand(factionName);
        int power = database.getFactionPower(factionName);
        int maxPower = database.getMaxFactionPower(factionName);
        int landValue = database.getFactionLandValue(factionName);
        double balance = database.getFactionBalance(factionName);
        int spawners = database.getFactionSpawners(factionName);
        int allies = database.getFactionAlliesCount(factionName);
        int totalMembers = database.getUsersInFactionCount(factionName);

        int onlineMembers = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String playerName = onlinePlayer.getName();
            String playerFaction = database.getUserFactionName(playerName);
            if (playerFaction != null && playerFaction.equals(factionName)) {
                onlineMembers++;
            }
        }

        player.sendMessage(ChatColor.GREEN + "Faction Details: " + factionName);
        player.sendMessage(ChatColor.YELLOW + "Description: " + description);
        player.sendMessage(ChatColor.YELLOW + "Join: " + (isInviteOnly ? "Invite Only" : "Open"));
        player.sendMessage(ChatColor.YELLOW + "Land/Power/MaxPower: " + land + "/" + power + "/" + maxPower);
        player.sendMessage(ChatColor.YELLOW + "Land Value: " + landValue);
        player.sendMessage(ChatColor.YELLOW + "Balance: " + balance);
        player.sendMessage(ChatColor.YELLOW + "Spawners: " + spawners);
        player.sendMessage(ChatColor.YELLOW + "Allies: " + allies + "/∞");
        player.sendMessage(ChatColor.YELLOW + "Online: " + onlineMembers + "/" + totalMembers);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String playerName = onlinePlayer.getName();
            String playerRank = database.getUserRank(playerName);
            if (database.getUserFactionName(playerName).equals(factionName)) {
                StringBuilder onlineUserString = new StringBuilder();

                if (playerRank.equalsIgnoreCase("founder")) {
                    onlineUserString.append(ChatColor.GOLD).append("*** ");
                } else if (playerRank.equalsIgnoreCase("coleader")) {
                    onlineUserString.append(ChatColor.GOLD).append("** ");
                } else if (playerRank.equalsIgnoreCase("moderator")) {
                    onlineUserString.append(ChatColor.GOLD).append("* ");
                }

                onlineUserString.append(playerName);

                player.sendMessage(onlineUserString.toString());
            }
        }
        return true;
    }

}
