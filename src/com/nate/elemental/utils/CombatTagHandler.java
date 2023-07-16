package com.nate.elemental.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;

public class CombatTagHandler implements Listener {
    private final Map<UUID, Long> combatCooldowns;
    private final Map<UUID, Villager> combatLoggers;

    public CombatTagHandler() {
        this.combatCooldowns = new HashMap<>();
        this.combatLoggers = new HashMap<>();
    }

    private void spawnCombatLogger(Player player) {
        Location location = player.getLocation();
        Villager loggerVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        loggerVillager.setCustomName(player.getName());
        loggerVillager.setCustomNameVisible(true);

        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] playerInventoryContents = playerInventory.getContents();
        ItemStack[] playerArmorContents = playerInventory.getArmorContents();

        ItemStack villagerSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) villagerSkull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(player.getName());
        villagerSkull.setItemMeta(skullMeta);

        loggerVillager.getInventory().setContents(playerInventoryContents);
        loggerVillager.getEquipment().setArmorContents(playerArmorContents);

        playerInventory.clear();
        playerInventory.setArmorContents(null);

        combatLoggers.put(player.getUniqueId(), loggerVillager);
    }

    @SuppressWarnings("unused")
	private void restorePlayerInventory(Player player, Villager villager) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] villagerInventoryContents = villager.getInventory().getContents();
        ItemStack[] villagerArmorContents = villager.getEquipment().getArmorContents();

        playerInventory.setContents(villagerInventoryContents);
        playerInventory.setArmorContents(villagerArmorContents);

        villager.remove();

        combatLoggers.remove(player.getUniqueId());
    }

    private void broadcastCombatLogMessage(String playerName) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + playerName + " logged out while in combat!");
    }

    private boolean isInSameFaction(Player player1, Player player2) {
        String faction1 = getFactionName(player1);
        String faction2 = getFactionName(player2);

        return faction1.equals(faction2) && !faction1.equals("wilderness");
    }

    private String getFactionName(Player player) {
        String playerName = player.getName();
        Database database = new Database();
        return database.getUserFactionName(playerName);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (combatCooldowns.containsKey(playerUUID)) {
            long currentTime = System.currentTimeMillis();
            long logoutTime = combatCooldowns.get(playerUUID);

            if (currentTime - logoutTime >= 10000) {
                combatCooldowns.remove(playerUUID);
                spawnCombatLogger(player);
                broadcastCombatLogMessage(player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attackedPlayer = (Player) event.getEntity();
            Player attackingPlayer = (Player) event.getDamager();

            String attackedFaction = getFactionName(attackedPlayer);
            String attackingFaction = getFactionName(attackingPlayer);

            if (!isInSameFaction(attackedPlayer, attackingPlayer) || !FactionUtils.areFactionsAllied(attackingFaction, attackedFaction)) {
                UUID attackedUUID = attackedPlayer.getUniqueId();
                combatCooldowns.put(attackedUUID, System.currentTimeMillis());
                attackedPlayer.sendMessage(ChatColor.RED + "You are now combat tagged!");
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        if (combatCooldowns.containsKey(playerUUID)) {
            long currentTime = System.currentTimeMillis();
            long logoutTime = combatCooldowns.get(playerUUID);

            if (currentTime - logoutTime >= 10000) {
                combatCooldowns.remove(playerUUID);
                spawnCombatLogger(player);
                broadcastCombatLogMessage(player.getName());
            }
        }
    }
}
