package com.nate.elemental.commands.factions;

import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;
import com.nate.elemental.utils.storage.h2.TableUtils;

public class ShowCommand implements CommandExecutor {
    Database database = new Database();
    TableUtils tableUtils = new TableUtils();
    
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
        
        String description = database.getFactionDescription(factionName);
        boolean isInviteOnly = database.isFactionInviteOnly(factionName);
        int land = database.getFactionLand(factionName);
        int power = database.getFactionPower(factionName);
        int maxPower = database.getMaxFactionPower(factionName);
        int landValue = database.getFactionLandValue(factionName);
        double balance = database.getFactionBalance(factionName);
        int spawners = database.getFactionSpawners(factionName);
        int alliesCount = tableUtils.getFactionAlliesCount(factionName);
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

        String alliesPrefix = ChatColor.YELLOW + "Allies(" + alliesCount + "):";
        player.sendMessage(alliesPrefix);

        String allies = FactionUtils.getFactionAllies(factionName);
        if (allies == null || allies.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "Allies: (0)");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Allies(" + alliesCount + "):");
            player.sendMessage(ChatColor.GOLD + allies);
        }

        player.sendMessage(ChatColor.YELLOW + "Online: " + onlineMembers + "/" + totalMembers);

        StringJoiner onlinePlayersJoiner = new StringJoiner(", ");
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
                onlinePlayersJoiner.add(onlineUserString.toString());
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Players: " + onlinePlayersJoiner.toString());

        return true;
    }
}
