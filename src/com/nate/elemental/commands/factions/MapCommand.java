package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.utils.storage.h2.Database;

public class MapCommand implements CommandExecutor {
    private final Database database;
    private final int chunkSizeX = 10;
    private final int chunkSizeZ = 14;

    public MapCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        int playerChunkX = player.getLocation().getChunk().getX();
        int playerChunkZ = player.getLocation().getChunk().getZ();

        int startX = playerChunkX - chunkSizeX / 2;
        int startZ = playerChunkZ - chunkSizeZ / 2;

        player.sendMessage(ChatColor.GREEN + "Chunk Map:");

        StringBuilder mapBuilder = new StringBuilder();

        for (int z = startZ; z < startZ + chunkSizeZ; z++) {
            StringBuilder line = new StringBuilder();
            for (int x = startX; x < startX + chunkSizeX; x++) {
                Chunk chunk = player.getWorld().getChunkAt(x, z);
                String chunkKey = getChunkKey(chunk);

                if (database.isChunkClaimed(chunkKey)) {
                    String factionName = database.getFactionNameByChunk(chunkKey);
                    char factionChar = getFactionChar(factionName);

                    if (x == playerChunkX && z == playerChunkZ) {
                        line.append(ChatColor.RED + "/" + ChatColor.RESET);
                    } else if (factionName.equals(database.getUserFactionName(player.getName()))) {
                        line.append(ChatColor.GREEN).append(factionChar);
                    } else {
                        line.append(factionChar);
                    }
                } else {
                    line.append("/");
                }
            }
            mapBuilder.append(line.toString()).append("\n");
        }

        player.sendMessage(mapBuilder.toString());

        sendFactionKey(player);

        return true;
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
    }

    private char getFactionChar(String factionName) {
        char factionChar = factionName.charAt(0);
        if (!Character.isLetter(factionChar)) {
            factionChar = '#'; // Use '#' for factions with non-letter names
        }
        return factionChar;
    }

    private void sendFactionKey(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Faction Key:");
        player.sendMessage(ChatColor.YELLOW + "A: Faction A");
        player.sendMessage(ChatColor.YELLOW + "B: Faction B");
        player.sendMessage(ChatColor.YELLOW + "C: Faction C");
    }
}
