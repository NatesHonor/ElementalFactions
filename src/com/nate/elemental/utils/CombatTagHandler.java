package com.nate.elemental.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class CombatTagHandler implements Listener {
    private final Factions plugin;
    private final Database database;
    private final Map<UUID, Long> combatCooldowns;
    private final Map<UUID, Villager> combatLoggers;

    public CombatTagHandler(Factions plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        this.combatCooldowns = new HashMap<>();
        this.combatLoggers = new HashMap<>();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();
        Player attackingPlayer = (Player) event.getDamager();

        String damagedPlayerFaction = database.getUserFactionName(damagedPlayer.getName());
        String attackingPlayerFaction = database.getUserFactionName(attackingPlayer.getName());

        if (damagedPlayerFaction != null && damagedPlayerFaction.equals(attackingPlayerFaction)) {
            event.setCancelled(true);
            attackingPlayer.sendMessage(ChatColor.RED + "You can't hurt a faction member");
            return;
        }

        combatCooldowns.put(damagedPlayer.getUniqueId(), System.currentTimeMillis() + 10000);

        Villager loggerVillager = spawnCombatLogger(damagedPlayer);
        combatLoggers.put(damagedPlayer.getUniqueId(), loggerVillager);

        new BukkitRunnable() {
            @Override
            public void run() {
                combatLoggers.remove(damagedPlayer.getUniqueId());
                loggerVillager.remove();
            }
        }.runTaskLater(plugin, 200);

        damagedPlayer.sendMessage(ChatColor.RED + "You are in combat! You cannot use /f home or /f warp.");
        damagedPlayer.sendMessage(ChatColor.RED + "If you log out, a villager with your name will appear.");
        damagedPlayer.sendMessage(ChatColor.RED + "Do not kill the villager or you will lose your items!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (combatCooldowns.containsKey(player.getUniqueId())) {
            broadcastCombatLogMessage(player.getName());
            
            player.getInventory().clear();
        }
    }

    private Villager spawnCombatLogger(Player player) {
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setCustomName(player.getName());
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCollidable(false);

        ItemStack[] playerInventoryContents = player.getInventory().getContents();
        ItemStack[] playerArmorContents = player.getInventory().getArmorContents();

        villager.getInventory().setContents(playerInventoryContents);
        villager.getEquipment().setHelmet(playerArmorContents[3]);
        villager.getEquipment().setChestplate(playerArmorContents[2]);
        villager.getEquipment().setLeggings(playerArmorContents[1]);
        villager.getEquipment().setBoots(playerArmorContents[0]);

        player.getInventory().clear();

        return villager;
    }



    private void broadcastCombatLogMessage(String playerName) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + playerName + " logged out while in combat!");
    }
}
