package com.nate.elemental.utils.shops.spawner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerBreakListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.SPAWNER) {
                CreatureSpawner spawner = (CreatureSpawner) clickedBlock.getState();
                EntityType entityType = spawner.getSpawnedType();
                String entityName = formatEntityName(entityType);

                disableSpawnerAI(spawner);

                openSpawnerGUI(player, entityName, spawner);
            }
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && item.getType() == Material.END_CRYSTAL) {
                player.sendMessage("You are not allowed to place End Crystals.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item != null && item.getType() == Material.END_CRYSTAL) {
            player.sendMessage("You are not allowed to have End Crystals in your inventory.");
            event.setCancelled(true);
            player.closeInventory();
            player.getInventory().remove(item);
        }
    }

    private String formatEntityName(EntityType entityType) {
        String entityName = entityType.name().toLowerCase().replace("_", " ");
        return ChatColor.GOLD + "♦ " + ChatColor.RED + entityName + ChatColor.GOLD + " ♦";
    }

    private void openSpawnerGUI(Player player, String entityName, CreatureSpawner spawner) {
        // Your GUI implementation
    }

    private void disableSpawnerAI(CreatureSpawner spawner) {
        // Disable AI logic
    }
}


