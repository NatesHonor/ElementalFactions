package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class ClaimCommand implements CommandExecutor, Listener {
    private final Factions plugin;
    private final Database database;

    public ClaimCommand(Factions plugin) {
        this.plugin = plugin;
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

        Chunk chunk = player.getLocation().getChunk();
        String chunkKey = getChunkKey(chunk);

        if (database.isChunkClaimed(chunkKey)) {
            player.sendMessage(ChatColor.RED + "This chunk is already claimed by another faction.");
            return true;
        }

        if (factionName == null) {
            factionName = "Wilderness";
        }

        database.claimChunk(factionName, chunkKey);

        player.sendMessage(ChatColor.GREEN + "Chunk claimed successfully.");

        displayFactionInformation(player, factionName);

        return true;
    }

    public static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
    }

    private void displayFactionInformation(Player player, String factionName) {
        String factionDescription = database.getFactionDescription(factionName);

        player.sendTitle(ChatColor.GREEN + factionName, factionDescription, 0, 80, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.resetTitle();
            }
        }.runTaskLater(plugin, 80);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        String fromChunkKey = getChunkKey(fromChunk);
        String toChunkKey = getChunkKey(toChunk);

        String fromFaction = database.getFactionNameByChunk(fromChunkKey);
        String toFaction = database.getFactionNameByChunk(toChunkKey);

        if (fromFaction == null && toFaction != null) {
            displayFactionInformation(player, toFaction);
        } else if (fromFaction != null && toFaction == null) {
            player.resetTitle();
        } else if (fromFaction != null && toFaction != null && !fromFaction.equals(toFaction)) {
            displayFactionInformation(player, toFaction);
        } else if (fromFaction == null && toFaction == null) {
            player.resetTitle();
        }
    }
}
