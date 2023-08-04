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

public class TrenchPickaxe implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("trenchpickaxe.give")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            giveTrenchPickaxe(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("other")) {
            if (!player.hasPermission("trenchpickaxe.give.other")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to give the Trench Pickaxe to others.");
                return true;
            }

            player.sendMessage(ChatColor.RED + "Usage: /trenchpickaxe other <player>");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("other")) {
            if (!player.hasPermission("trenchpickaxe.give.other")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to give the Trench Pickaxe to others.");
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                giveTrenchPickaxe(targetPlayer);
                player.sendMessage(ChatColor.GREEN + "You gave a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                        + ChatColor.YELLOW + " to " + targetPlayer.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Player not found or offline.");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Invalid command format. Usage: /trenchpickaxe [other <player>]");
        return true;
    }

    private void giveTrenchPickaxe(Player player) {
        ItemStack trenchPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = trenchPickaxe.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Trench Pickaxe ⛏️");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Size: " + ChatColor.YELLOW + "3x3",
                ChatColor.GRAY + "Uses: " + ChatColor.YELLOW + "Infinite"));
        trenchPickaxe.setItemMeta(meta);

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(trenchPickaxe);
            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                    + ChatColor.GREEN + " with size 3x3" + ChatColor.GREEN + " (Infinite uses).");
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), trenchPickaxe);
            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + "Trench Pickaxe ⛏️"
                    + ChatColor.GREEN + " with size 3x3" + ChatColor.GREEN + " (Infinite uses).");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.getType() == Material.DIAMOND_PICKAXE
                && itemInHand.getItemMeta() != null
                && itemInHand.getItemMeta().getDisplayName() != null
                && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Trench Pickaxe ⛏️")) {

            ItemMeta meta = itemInHand.getItemMeta();
            if (meta.hasLore()) {
                for (String line : meta.getLore()) {
                    if (line.startsWith(ChatColor.GRAY + "Size: ")) {
                        String sizeStr = ChatColor.stripColor(line.substring(7)).trim();
                        int size = Integer.parseInt(sizeStr);
                        event.setCancelled(true); // Cancel the original block break event
                        mineArea(player, event.getBlock().getLocation(), size);
                        break;
                    }
                }
            }
        }
    }

    private void mineArea(Player player, Location location, int size) {
        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() != Material.BEDROCK) {
                        block.breakNaturally(); // Use breakNaturally safely since the event is already cancelled
                    }
                }
            }
        }
    }
}
