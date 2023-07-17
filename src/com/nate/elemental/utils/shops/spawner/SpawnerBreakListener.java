package com.nate.elemental.utils.shops.spawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SpawnerBreakListener implements Listener {

    private final Spawner spawnerUtils;

    public SpawnerBreakListener() {
        this.spawnerUtils = new Spawner();
    }  

    @EventHandler
    public void onSpawnerBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.SPAWNER) {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            String entityName = spawnerUtils.getFormattedEntityName(spawner.getSpawnedType().name());
            Player player = event.getPlayer();
            player.sendMessage("You have broken a " + entityName + " Spawner!");
        }
    }
}

