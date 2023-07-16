package com.nate.elemental.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ThrowableCreeperItem {
    public static ItemStack createThrowableCreeperItem() {
        ItemStack throwableCreeper = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta meta = throwableCreeper.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Throwable Creeper");
        throwableCreeper.setItemMeta(meta);
        return throwableCreeper;
    }

    public static ItemStack createThrowableCreeperEgg() {
        ItemStack throwableCreeperEgg = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta meta = throwableCreeperEgg.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Throwable Creeper");
        throwableCreeperEgg.setItemMeta(meta);
        return throwableCreeperEgg;
    }

    public static void spawnThrowableCreeper(Location location) {
        Creeper creeper = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
        creeper.setPowered(true);
        creeper.setExplosionRadius(3);
        creeper.setAI(false);
        creeper.ignite();
    }
}
