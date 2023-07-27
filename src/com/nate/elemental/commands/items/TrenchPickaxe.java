package com.nate.elemental.commands.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class TrenchPickaxe implements CommandExecutor, Listener {

    public TrenchPickaxe() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            giveTrenchPickaxe(player, 3, -1); // Default size is 3x3, infinite uses
            return true;
        }

        if (args.length == 1) {
            // Check if the argument is a player name
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                giveTrenchPickaxe(targetPlayer, 3, -1); // Default size is 3x3, infinite uses
                player.sendMessage(ChatColor.GREEN + "You gave a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                        + ChatColor.YELLOW + " to " + targetPlayer.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Player not found or offline.");
            }
            return true;
        }

        int size = 3; // Default size is 3x3
        int uses = -1; // -1 means infinite uses

        if (args.length >= 1) {
            try {
                size = Integer.parseInt(args[0]);
                if (size != 3 && size != 5 && size != 10) {
                    player.sendMessage(ChatColor.RED + "Invalid size argument. The size must be 3, 5, or 10.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(
                        ChatColor.RED + "Invalid size argument. Please provide a valid number (3, 5, or 10).");
                return true;
            }

            if (args.length >= 2) {
                try {
                    uses = Integer.parseInt(args[1]);
                    if (uses <= 0) {
                        player.sendMessage(
                                ChatColor.RED + "Invalid uses argument. The number of uses must be greater than 0.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(
                            ChatColor.RED + "Invalid uses argument. Please provide a valid number greater than 0.");
                    return true;
                }
            }
        }

        giveTrenchPickaxe(player, size, uses);

        return true;
    }

    private void giveTrenchPickaxe(Player player, int size, int uses) {
        ItemStack trenchPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = trenchPickaxe.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Trench Pickaxe ⛏️");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Size: " + ChatColor.YELLOW + size + "x" + size,
                ChatColor.GRAY + "Uses: "
                        + (uses == -1 ? ChatColor.YELLOW + "Infinite" : ChatColor.YELLOW + "" + uses)));
        trenchPickaxe.setItemMeta(meta);

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(trenchPickaxe);
            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                    + ChatColor.GREEN + " with size " + ChatColor.YELLOW + size + "x" + size +
                    (uses == -1 ? ChatColor.GREEN + " (Infinite uses)."
                            : ChatColor.GREEN + " and " + ChatColor.YELLOW + uses + ChatColor.GREEN + " uses."));
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), trenchPickaxe);
            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                    + ChatColor.GREEN + " with size " + ChatColor.YELLOW + size + "x" + size +
                    (uses == -1 ? ChatColor.GREEN + " (Infinite uses)."
                            : ChatColor.GREEN + " and " + ChatColor.YELLOW + uses + ChatColor.GREEN + " uses."));
        }
    }

    private void mineArea(Player player, Location location, int size) {
        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() != Material.BEDROCK) {
                        block.breakNaturally();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.getType() == Material.DIAMOND_PICKAXE
                && itemInHand.getItemMeta() != null
                && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Trench Pickaxe ⛏️")) {

            ItemMeta meta = itemInHand.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String line : lore) {
                    if (line.startsWith(ChatColor.GRAY + "Size: ")) {
                        String sizeStr = ChatColor.stripColor(line.substring(7).split("x")[0]);
                        int size = Integer.parseInt(sizeStr);
                        mineArea(player, event.getBlock().getLocation(), size);
                        break;
                    }
                }
            }
        }
    }
}
