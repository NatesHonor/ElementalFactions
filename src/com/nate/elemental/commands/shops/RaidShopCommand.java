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
        } if (clickedItem.getType() == Material.CREEPER_SPAWN_EGG) {
                        ItemStack chargedCreeperEgg = createChargedCreeperEgg();
                        player.getInventory().addItem(chargedCreeperEgg);
                        player.sendMessage(ChatColor.GREEN + "You have acquired a Raid Creeper!");
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
        }
    }

    private Inventory createRaidShopInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Raid Shop");

        ItemStack fireballItem = createFireballItem();
        inventory.setItem(10, fireballItem);

        ItemStack raidCreeperItem = createRaidCreeperItem();
        inventory.setItem(13, raidCreeperItem);

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
}





