package com.nate.elemental.utils.shops.spawner;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerSpawnListener implements Listener {

    private final Map<LivingEntity, Integer> stackedEntities;

    public SpawnerSpawnListener() {
        this.stackedEntities = new HashMap<>();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && entity.getType() != EntityType.PLAYER) {
            LivingEntity livingEntity = (LivingEntity) entity;

            if (stackedEntities.containsKey(livingEntity)) {
                int stackSize = stackedEntities.get(livingEntity);

                // Multiply the loot drops by the stack size
                for (ItemStack drop : event.getDrops()) {
                    ItemStack multipliedDrop = drop.clone();
                    multipliedDrop.setAmount(multipliedDrop.getAmount() * stackSize);
                    event.getDrops().add(multipliedDrop);
                }

                stackedEntities.remove(livingEntity);
                livingEntity.remove();
            } else {
                CreatureSpawner spawner = getNearbySpawner(livingEntity);
                if (spawner != null) {
                  SpawnerPlaceListener spawnerPlaceListener = new SpawnerPlaceListener();
                    int stackSize = spawnerPlaceListener.getSpawnerStackSize(spawner);
                    if (stackSize > 1) {
                        stackedEntities.put(livingEntity, stackSize);
                        livingEntity.setCustomName(entity.getName() + " x" + stackSize);
                    }
                }
            }
        }
    }

    private CreatureSpawner getNearbySpawner(LivingEntity entity) {
        int radius = 5;
        for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity.getType() == EntityType.MINECART_MOB_SPAWNER) {
                return (CreatureSpawner) nearbyEntity;
            }
        }
        return null;
    }
}
