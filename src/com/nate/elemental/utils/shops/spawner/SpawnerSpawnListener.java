package com.nate.elemental.utils.shops.spawner;

import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnerSpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            CreatureSpawner spawner = getSpawnerFromEntity(livingEntity);
                formatEntityName(livingEntity, spawner);
                disableEntityAI(livingEntity);
                disableEntityMovement(livingEntity);
            }
    }

    private CreatureSpawner getSpawnerFromEntity(LivingEntity entity) {
        CreatureSpawner spawner = null;
        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof LivingEntity) {
            LivingEntity vehicleEntity = (LivingEntity) vehicle;
            if (vehicleEntity instanceof CreatureSpawner) {
                spawner = (CreatureSpawner) vehicleEntity;
            }
        }
        return spawner;
    }

    private void formatEntityName(LivingEntity entity, CreatureSpawner spawner) {
        String entityName = spawner.getSpawnedType().name();
        entityName = entityName.substring(0, 1).toUpperCase() + entityName.substring(1).toLowerCase();
        entityName = ChatColor.GOLD + "♦ " + ChatColor.RED + entityName + ChatColor.GOLD + " ♦";
        entity.setCustomName(entityName);
        entity.setCustomNameVisible(true);
    }

    private void disableEntityAI(LivingEntity entity) {
        entity.setAI(false);
    }

    private void disableEntityMovement(LivingEntity entity) {
        entity.setGravity(false);
        entity.setCollidable(false);
    }
}
