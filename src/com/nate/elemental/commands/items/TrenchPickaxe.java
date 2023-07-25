package com.nate.elemental.commands.items;

import org.bukkit.Bukkit;
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
            sender.sendMessage("This command can only be executed by players.");
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
                player.sendMessage("§aYou gave a §6Trench Pickaxe §e⛏️ §ato " + targetPlayer.getName());
            } else {
                player.sendMessage("§cPlayer not found or offline.");
            }
            return true;
        }

        int size = 3; // Default size is 3x3
        int uses = -1; // -1 means infinite uses

        if (args.length >= 1) {
            try {
                size = Integer.parseInt(args[0]);
                if (size != 3 && size != 5 && size != 10) {
                    player.sendMessage("Invalid size argument. The size must be 3, 5, or 10.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid size argument. Please provide a valid number (3, 5, or 10).");
                return true;
            }

            if (args.length >= 2) {
                try {
                    uses = Integer.parseInt(args[1]);
                    if (uses <= 0) {
                        player.sendMessage("Invalid uses argument. The number of uses must be greater than 0.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid uses argument. Please provide a valid number greater than 0.");
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
        meta.setDisplayName("§6Trench Pickaxe §e⛏️");
        meta.setLore(Arrays.asList("§7Size: §e" + size + "x" + size,
                "§7Uses: " + (uses == -1 ? "§eInfinite" : "§e" + uses)));
        trenchPickaxe.setItemMeta(meta);

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(trenchPickaxe);
            player.sendMessage("§aYou received a §6Trench Pickaxe §e⛏️§a with size §e" + size + "x" + size +
                    (uses == -1 ? "§a (Infinite uses)." : "§a and §e" + uses + "§a uses."));
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), trenchPickaxe);
            player.sendMessage("§aYou received a §6Trench Pickaxe §e⛏️§a with size §e" + size + "x" + size +
                    (uses == -1 ? "§a (Infinite uses)." : "§a and §e" + uses + "§a uses."));
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
                && itemInHand.getItemMeta().getDisplayName().equals("§6Trench Pickaxe §e⛏️")) {

            ItemMeta meta = itemInHand.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String line : lore) {
                    if (line.startsWith("§7Size: ")) {
                        int size = Integer.parseInt(line.substring(7).split("x")[0]);
                        mineArea(player, event.getBlock().getLocation(), size);
                        break;
                    }
                }
            }
        }
    }
}
