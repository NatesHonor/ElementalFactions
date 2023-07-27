package com.nate.elemental.commands.factions;

import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;
import com.nate.elemental.utils.storage.h2.TableUtils;

public class ShowCommand implements CommandExecutor, Listener {
    private final Database database;
    private final TableUtils tableUtils;
    private final FactionUtils factionUtils;

    public ShowCommand() {
        this.database = new Database();
        this.tableUtils = new TableUtils();
        this.factionUtils = new FactionUtils();
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
        int landValue = database.getFactionLandValue(factionName);
        double balance = database.getFactionBalance(factionName);
        int spawners = database.getFactionSpawners(factionName);
        int alliesCount = tableUtils.getFactionAlliesCount(factionName);
        int totalMembers = database.getUsersInFactionCount(factionName);

        int onlineMembers = 0;
        int offlineMembers = 0;

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();
            String playerFaction = database.getUserFactionName(playerName);
            if (playerFaction != null && playerFaction.equals(factionName)) {
                if (offlinePlayer.isOnline()) {
                    onlineMembers++;
                } else {
                    offlineMembers++;
                }
            }
        }

        int maxPower = totalMembers * 10;

        player.sendMessage(ChatColor.GREEN + "Faction Details: " + factionName);
        player.sendMessage(ChatColor.YELLOW + "Description: " + description);
        player.sendMessage(ChatColor.YELLOW + "Join: " + (isInviteOnly ? "Invite Only" : "Open"));
        player.sendMessage(ChatColor.YELLOW + "Land/Power/MaxPower: " + land + "/" + power + "/" + maxPower);
        player.sendMessage(ChatColor.YELLOW + "Land Value: " + landValue);
        player.sendMessage(ChatColor.YELLOW + "Balance: " + balance);
        player.sendMessage(ChatColor.YELLOW + "Spawners: " + spawners);

        String allies = FactionUtils.getFactionAllies(factionName);
        if (allies == null || allies.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "Allies: (0)");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Allies(" + alliesCount + "):");
            player.sendMessage(ChatColor.GOLD + allies);
        }

        player.sendMessage(ChatColor.YELLOW + "Online: (" + onlineMembers + "/" + totalMembers + ")");

        StringJoiner onlinePlayersJoiner = new StringJoiner(", ");
        StringJoiner offlinePlayersJoiner = new StringJoiner(", ");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String playerName = onlinePlayer.getName();
            String playerRank = database.getUserRank(playerName);
            String playerPrefix = factionUtils.getRankPrefix(factionName, playerRank);
            if (database.getUserFactionName(playerName).equals(factionName)) {
                StringBuilder playerString = new StringBuilder();

                if (playerPrefix != null && !playerPrefix.isEmpty()) {
                    playerPrefix = ChatColor.translateAlternateColorCodes('&', playerPrefix + " ");
                    playerString.append(playerPrefix);
                } else {
                    if (playerRank.equalsIgnoreCase("founder")) {
                        playerString.append(ChatColor.GOLD).append("*** ");
                    } else if (playerRank.equalsIgnoreCase("coleader")) {
                        playerString.append(ChatColor.GOLD).append("** ");
                    } else if (playerRank.equalsIgnoreCase("moderator")) {
                        playerString.append(ChatColor.GOLD).append("* ");
                    }
                }

                playerString.append(playerName);
                onlinePlayersJoiner.add(playerString.toString());
            }
        }

        player.sendMessage(ChatColor.YELLOW + onlinePlayersJoiner.toString());

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();
            String playerRank = database.getUserRank(playerName);
            String playerPrefix = factionUtils.getRankPrefix(factionName, playerRank);
            if (database.getUserFactionName(playerName).equals(factionName) && !offlinePlayer.isOnline()) {
                StringBuilder playerString = new StringBuilder();

                if (playerPrefix != null && !playerPrefix.isEmpty()) {
                    playerPrefix = ChatColor.translateAlternateColorCodes('&', playerPrefix + " ");
                    playerString.append(playerPrefix);
                } else {
                    if (playerRank.equalsIgnoreCase("founder")) {
                        playerString.append(ChatColor.GOLD).append("*** ");
                    } else if (playerRank.equalsIgnoreCase("coleader")) {
                        playerString.append(ChatColor.GOLD).append("** ");
                    } else if (playerRank.equalsIgnoreCase("moderator")) {
                        playerString.append(ChatColor.GOLD).append("* ");
                    }
                }

                playerString.append(playerName);
                offlinePlayersJoiner.add(playerString.toString());
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Players Offline: " + offlineMembers + "/" + totalMembers);
        player.sendMessage(ChatColor.YELLOW + offlinePlayersJoiner.toString());

        return true;
    }
}
