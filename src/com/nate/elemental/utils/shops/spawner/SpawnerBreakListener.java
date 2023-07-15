package com.nate.elemental.utils.shops.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SpawnerBreakListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SPAWNER) {
            Location spawnerLocation = event.getBlock().getLocation();
            deleteHologram(spawnerLocation);
        }
    }

    private void deleteHologram(Location spawnerLocation) {
        double armorStandY = spawnerLocation.getY() + 1;

        ArmorStand nearestArmorStand = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : spawnerLocation.getWorld().getEntitiesByClass(ArmorStand.class)) {
            double distance = entity.getLocation().distanceSquared(spawnerLocation);
            if (distance < nearestDistance) {
                nearestArmorStand = (ArmorStand) entity;
                nearestDistance = distance;
            }
        }

        if (nearestArmorStand != null && nearestArmorStand.getLocation().getY() == armorStandY) {
            nearestArmorStand.remove();
        }
    }

}
