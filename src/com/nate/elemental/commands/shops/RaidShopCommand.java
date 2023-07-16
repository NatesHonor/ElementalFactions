package com.nate.elemental.commands.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nate.elemental.Factions;
import com.nate.elemental.items.FireballItem;

import net.milkbowl.vault.economy.Economy;

public class RaidShopCommand implements CommandExecutor, Listener {
    private final Factions plugin;

    public RaidShopCommand(Factions factions) {
        this.plugin = factions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        Inventory shopInventory = createRaidShopInventory();
        player.openInventory(shopInventory);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Raid Shop")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (clickedItem.getType() == Material.FIRE_CHARGE) {
            double fireballCost = plugin.getConfig().getDouble("Raid-Shop.fireball");

            Economy economy = plugin.getEconomy();
            if (economy.has(player, fireballCost)) {
                economy.withdrawPlayer(player, fireballCost);
                FireballItem.giveFireball(player);

                player.sendMessage(ChatColor.GREEN + "You have purchased a fireball from the Raid Shop!");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have enough money to purchase a fireball!");
            }
        } else if (clickedItem.getType() == Material.CREEPER_SPAWN_EGG) {
            ItemStack chargedCreeperEgg = createChargedCreeperEgg();
            player.getInventory().addItem(chargedCreeperEgg);
            player.sendMessage(ChatColor.GREEN + "You have acquired a Raid Creeper!");
        } else if (clickedItem.getType() == Material.TNT) {
            ItemStack instantExplodeCreeper = createInstantExplodeCreeper();
            player.getInventory().addItem(instantExplodeCreeper);
            player.sendMessage(ChatColor.GREEN + "You have acquired an Instant Explode Creeper!");
        } else if (clickedItem.getType() == Material.GUNPOWDER) {
            ItemStack throwableCreeper = createThrowableCreeper();
            player.getInventory().addItem(throwableCreeper);
            player.sendMessage(ChatColor.GREEN + "You have acquired a Throwable Creeper!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.CREEPER_SPAWN_EGG && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            String displayName = ChatColor.translateAlternateColorCodes('&', "&2Raid Creeper");

            if (meta.hasDisplayName() && meta.getDisplayName().equals(displayName)) {
                event.setCancelled(true);

                Location location = event.getClickedBlock().getLocation();
                Creeper creeper = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
                creeper.setPowered(true);

                int explosionRadius = 3;
                creeper.setExplosionRadius(explosionRadius);

                creeper.setAI(false);

                creeper.ignite();

                player.getInventory().remove(item);
            }
        } else if (item != null && item.getType() == Material.TNT) {
            event.setCancelled(true);

            Location location = event.getClickedBlock().getLocation();
            location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);

            player.getInventory().remove(item);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.GUNPOWDER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            String displayName = ChatColor.translateAlternateColorCodes('&', "&9Throwable Creeper");

            if (meta.hasDisplayName() && meta.getDisplayName().equals(displayName)) {
                Location location = event.getEntity().getLocation();
                Creeper creeper = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
                creeper.setPowered(true);

                int explosionRadius = 3;
                creeper.setExplosionRadius(explosionRadius);

                creeper.setAI(false);

                creeper.ignite();
            }
        }
    }

    private Inventory createRaidShopInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Raid Shop");

        ItemStack fireballItem = createFireballItem();
        inventory.setItem(10, fireballItem);

        ItemStack raidCreeperItem = createRaidCreeperItem();
        inventory.setItem(13, raidCreeperItem);

        ItemStack instantExplodeCreeperItem = createInstantExplodeCreeper();
        inventory.setItem(16, instantExplodeCreeperItem);

        ItemStack throwableCreeperItem = createThrowableCreeper();
        inventory.setItem(19, throwableCreeperItem);

        return inventory;
    }

    private ItemStack createRaidCreeperItem() {
        ItemStack raidCreeper = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta meta = raidCreeper.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Raid Creeper");
        raidCreeper.setItemMeta(meta);
        return raidCreeper;
    }

    private ItemStack createFireballItem() {
        ItemStack fireball = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = fireball.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Fireball");
        fireball.setItemMeta(meta);
        return fireball;
    }

    private ItemStack createChargedCreeperEgg() {
        ItemStack chargedCreeperEgg = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta meta = chargedCreeperEgg.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Raid Creeper");
        chargedCreeperEgg.setItemMeta(meta);
        return chargedCreeperEgg;
    }

    private ItemStack createInstantExplodeCreeper() {
        ItemStack instantExplodeCreeper = new ItemStack(Material.TNT);
        ItemMeta meta = instantExplodeCreeper.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Instant Explode Creeper");
        instantExplodeCreeper.setItemMeta(meta);
        return instantExplodeCreeper;
    }

    private ItemStack createThrowableCreeper() {
        ItemStack throwableCreeper = new ItemStack(Material.GUNPOWDER);
        ItemMeta meta = throwableCreeper.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Throwable Creeper");
        throwableCreeper.setItemMeta(meta);
        return throwableCreeper;
    }
}
