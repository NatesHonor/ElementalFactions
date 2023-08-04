package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nate.elemental.utils.storage.h2.Chunkutils;
import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;

import java.util.HashMap;
import java.util.Map;

public class MapCommand implements CommandExecutor, Listener {
    Database database = new Database();
    Chunkutils chunkUtils = new Chunkutils();
    FactionUtils factionUtils = new FactionUtils();
    private final int chunkSizeX = 16;
    private final int chunkSizeZ = 10;

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

        Map<String, ChatColor> factionColors = new HashMap<>(); // Store faction colors
        int colorIndex = 0;

        for (int z = startZ; z < startZ + chunkSizeZ; z++) {
            StringBuilder line = new StringBuilder();
            for (int x = startX; x < startX + chunkSizeX; x++) {
                Chunk chunk = player.getWorld().getChunkAt(x, z);
                String chunkKey = getChunkKey(chunk);

                if (chunkUtils.isChunkClaimed(chunkKey)) {
                    String factionName = chunkUtils.getFactionNameByChunk(chunkKey);
                    char factionChar = getFactionChar(factionName);

                    if (!factionColors.containsKey(factionName)) {
                        ChatColor factionColor = getColorForIndex(colorIndex);
                        factionColors.put(factionName, factionColor);
                        colorIndex++;
                    }

                    ChatColor factionColor = factionColors.get(factionName);

                    if (x == playerChunkX && z == playerChunkZ) {
                        line.append(ChatColor.RED + "/" + ChatColor.RESET);
                    } else if (factionName.equals(database.getUserFactionName(player.getName()))) {
                        line.append(factionColor).append(factionChar).append(ChatColor.RESET);
                    } else {
                        line.append(factionColor).append(factionChar).append(ChatColor.RESET);
                    }
                } else {
                    line.append("/");
                }
            }
            mapBuilder.append(line.toString()).append("\n");
        }

        player.sendMessage(mapBuilder.toString());

        // Display faction key based on currently displayed faction
        String displayedFactionName = chunkUtils.getFactionNameByChunk(getChunkKey(player.getLocation().getChunk()));
        if (displayedFactionName != null) {
            player.sendMessage(ChatColor.YELLOW + "Faction Key:");
            for (String factionName : factionColors.keySet()) {
                ChatColor factionColor = factionColors.get(factionName);
                player.sendMessage(factionColor + factionName + ": " + getFactionChar(factionName) + ChatColor.RESET);
            }
        }

        return true;
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
    }

    private char getFactionChar(String factionName) {
        char factionChar = factionName.charAt(0);
        if (!Character.isLetter(factionChar)) {
            factionChar = '#';
        }
        return factionChar;
    }

    private ChatColor getColorForIndex(int index) {
        ChatColor[] colors = {
                ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
                ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.WHITE
                // Add more colors as needed
        };

        return colors[index % colors.length];
    }
}
