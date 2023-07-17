package com.nate.elemental.utils.shops.spawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerPlaceListener implements Listener {

    private final Spawner spawnerUtils;

    public SpawnerPlaceListener() {
        this.spawnerUtils = new Spawner();
    }  

    @EventHandler
    public void onSpawnerPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.SPAWNER) {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            String entityName = spawnerUtils.getFormattedEntityName(spawner.getSpawnedType().name());
            event.getPlayer().sendMessage("You have placed a " + entityName + " Spawner!");
        }
    }

}
