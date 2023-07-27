package com.nate.elemental.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nate.elemental.Factions;

public class PearlCooldownHandler implements Listener {
    private final Map<UUID, Long> pearlCooldowns;
    private final Factions plugin;

    public PearlCooldownHandler() {
        this.pearlCooldowns = new HashMap<>();
        this.plugin = Factions.getInstance();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item != null && item.getType().equals(Material.ENDER_PEARL)) {
            if (hasPearlCooldown(player)) {
                long remainingTime = getPearlCooldownRemaining(player);

                player.sendMessage(ChatColor.RED + "You're on a pearl cooldown! " + remainingTime + "s remaining.");
                event.setCancelled(true);
                return;
            }
            applyPearlCooldown(player);
            schedulePearlCooldownExpiration(player);
        }
    }

    private void applyPearlCooldown(Player player) {
        pearlCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
    }

    private boolean hasPearlCooldown(Player player) {
        return pearlCooldowns.containsKey(player.getUniqueId());
    }

    private long getPearlCooldownRemaining(Player player) {
        long cooldownExpiration = pearlCooldowns.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();
        return Math.max(0, (cooldownExpiration - currentTime) / 1000);
    }

    private void schedulePearlCooldownExpiration(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                pearlCooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 100);
    }
}
