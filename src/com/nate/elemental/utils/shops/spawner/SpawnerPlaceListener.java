package com.nate.elemental.utils.shops.spawner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SpawnerPlaceListener implements Listener {

    private final Map<CreatureSpawner, Integer> spawnerStackMap;

    public SpawnerPlaceListener() {
        this.spawnerStackMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.SPAWNER) {
                CreatureSpawner spawner = (CreatureSpawner) clickedBlock.getState();
                EntityType entityType = spawner.getSpawnedType();
                String entityName = capitalizeEntityName(entityType);

                openSpawnerGUI(event.getPlayer(), entityName, spawner);
            }
        }
    }

    private String capitalizeEntityName(EntityType entityType) {
        String entityName = entityType.name();
        String[] nameParts = entityName.split("_");
        StringBuilder capitalized = new StringBuilder();
        for (String part : nameParts) {
            capitalized.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return capitalized.toString().trim();
    }

    private void openSpawnerGUI(Player player, String entityName, CreatureSpawner spawner) {
        Inventory gui = createGUI(entityName, spawner);
        player.openInventory(gui);
    }

    private Inventory createGUI(String entityName, CreatureSpawner spawner) {
        Inventory gui = Bukkit.createInventory(null, 9, entityName + " Spawner");

        // Set head item in the center
        ItemStack headItem = createHeadItem(entityName);
        gui.setItem(4, headItem);

        // Add information about spawner
        ItemStack stackInfoItem = createStackInfoItem(spawner);
        gui.setItem(1, stackInfoItem);

        // Add boosters item
        ItemStack boostersItem = createBoostersItem();
        gui.setItem(7, boostersItem);

        return gui;
    }

    private ItemStack createHeadItem(String entityName) {
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta itemMeta = headItem.getItemMeta();
        itemMeta.setDisplayName(entityName + " Head");
        headItem.setItemMeta(itemMeta);
        return headItem;
    }

    private ItemStack createStackInfoItem(CreatureSpawner spawner) {
        ItemStack stackInfoItem = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = stackInfoItem.getItemMeta();
        itemMeta.setDisplayName("Spawner Info");

        int stackSize = getSpawnerStackSize(spawner);
        int spawnCount = spawner.getSpawnCount();

        String stackSizeLine = "Stack Size: " + stackSize;
        String spawnCountLine = "Spawn Count: " + spawnCount;

        itemMeta.setLore(Arrays.asList(stackSizeLine, spawnCountLine));
        stackInfoItem.setItemMeta(itemMeta);

        return stackInfoItem;
    }

    int getSpawnerStackSize(CreatureSpawner spawner) {
        return spawnerStackMap.getOrDefault(spawner, 1);
    }

    private ItemStack createBoostersItem() {
        ItemStack boostersItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta itemMeta = boostersItem.getItemMeta();
        itemMeta.setDisplayName("Boosters");
        boostersItem.setItemMeta(itemMeta);
        return boostersItem;
    }

    public void stackSpawner(CreatureSpawner spawner) {
        int stackSize = spawnerStackMap.getOrDefault(spawner, 1);
        stackSize++;
        spawnerStackMap.put(spawner, stackSize);

        updateSpawnerInfo(spawner);
    }

    public void unstackSpawner(CreatureSpawner spawner) {
        int stackSize = spawnerStackMap.getOrDefault(spawner, 1);
        if (stackSize > 1) {
            stackSize--;
            spawnerStackMap.put(spawner, stackSize);
        } else {
            spawnerStackMap.remove(spawner);
        }

        updateSpawnerInfo(spawner);
    }

    private void updateSpawnerInfo(CreatureSpawner spawner) {
        Inventory inventory = getInventoryForSpawner(spawner);
        if (inventory != null) {
            ItemStack stackInfoItem = createStackInfoItem(spawner);
            inventory.setItem(1, stackInfoItem);
        }
    }

    private Inventory getInventoryForSpawner(CreatureSpawner spawner) {
        String entityName = capitalizeEntityName(spawner.getSpawnedType());

        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView openInventory = player.getOpenInventory();
            String inventoryTitle = openInventory.getTitle();
            if (inventoryTitle.equals(entityName + " Spawner")) {
                Inventory topInventory = openInventory.getTopInventory();
                ItemStack headItem = topInventory.getItem(4);
                if (headItem != null && headItem.getType() == Material.PLAYER_HEAD) {
                    SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
                    if (skullMeta != null) {
                        String skullOwner = skullMeta.getOwningPlayer().getName();
                        if (skullOwner != null && skullOwner.equals(entityName)) {
                            return topInventory;
                        }
                    }
                }
            }
        }
        return null;
    }
}
