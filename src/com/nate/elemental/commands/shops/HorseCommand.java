package com.nate.elemental.commands.shops;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HorseCommand implements CommandExecutor, Listener {

    private final Map<Player, Integer> speedUpgrades = new HashMap<>();
    private final Map<Player, Long> spawnCooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Horse Upgrades");

        ItemStack speedUpgradeItem = new ItemStack(Material.SUGAR);
        ItemMeta speedUpgradeItemMeta = speedUpgradeItem.getItemMeta();
        speedUpgradeItemMeta.setDisplayName(ChatColor.GREEN + "Speed Upgrade");
        speedUpgradeItem.setItemMeta(speedUpgradeItemMeta);

        inventory.setItem(0, speedUpgradeItem);

        ItemStack spawnEggItem = new ItemStack(Material.HORSE_SPAWN_EGG);
        ItemMeta spawnEggItemMeta = spawnEggItem.getItemMeta();
        spawnEggItemMeta.setDisplayName(ChatColor.GREEN + "Spawn Horse");
        spawnEggItem.setItemMeta(spawnEggItemMeta);

        inventory.setItem(8, spawnEggItem);

        player.openInventory(inventory);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Horse Upgrades")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.SUGAR) {
                    upgradeSpeed(player);
                } else if (clickedItem.getType() == Material.HORSE_SPAWN_EGG) {
                    spawnHorse(player);
                }
            }
        }
    }

    private void upgradeSpeed(Player player) {
        int currentUpgrade = speedUpgrades.getOrDefault(player, 0);
        speedUpgrades.put(player, currentUpgrade + 1);
        player.sendMessage(ChatColor.GREEN + "You upgraded the speed of Nate's WarHorse!");
    }

    private void spawnHorse(Player player) {
        if (isOnCooldown(player)) {
            long remainingTime = getRemainingCooldown(player);
            player.sendMessage(ChatColor.RED + "You can't spawn the horse again for " + (remainingTime / 1000) + " seconds.");
            return;
        }

        int speedUpgrades = this.speedUpgrades.getOrDefault(player, 0);

        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        double baseSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
        double upgradedSpeed = baseSpeed + speedUpgrades;
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(upgradedSpeed);
        horse.setCustomName(ChatColor.YELLOW + "Nate's WarHorse");
        horse.setCustomNameVisible(true);

        horse.addPassenger(player);

        horse.setOwner(player);

        player.sendMessage(ChatColor.GREEN + "You spawned Nate's WarHorse with upgraded speed: " + upgradedSpeed);

        startCooldown(player);
    }

    private boolean isOnCooldown(Player player) {
        if (spawnCooldowns.containsKey(player)) {
            long lastSpawnTime = spawnCooldowns.get(player);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastSpawnTime;
            return elapsedTime < COOLDOWN_DURATION;
        }
        return false;
    }

    private void startCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        spawnCooldowns.put(player, currentTime);
    }

    private long getRemainingCooldown(Player player) {
        long lastSpawnTime = spawnCooldowns.getOrDefault(player, 0L);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastSpawnTime;
        return COOLDOWN_DURATION - elapsedTime;
    }

}
