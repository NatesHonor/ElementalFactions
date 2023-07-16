package com.nate.elemental.commands.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import com.nate.elemental.items.ThrowableCreeperItem;

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
            ItemStack throwableCreeperEgg = ThrowableCreeperItem.createThrowableCreeperEgg();
            player.getInventory().addItem(throwableCreeperEgg);
            player.sendMessage(ChatColor.GREEN + "You have acquired a Throwable Creeper!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.CREEPER_SPAWN_EGG && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            String displayName = ChatColor.translateAlternateColorCodes('&', "&2Throwable Creeper");

            if (meta.hasDisplayName() && meta.getDisplayName().equals(displayName)) {
                event.setCancelled(true);

                Location location = event.getClickedBlock().getLocation();
                ThrowableCreeperItem.spawnThrowableCreeper(location);

                player.getInventory().remove(item);
            }
        }
    }

    private Inventory createRaidShopInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Raid Shop");

        ItemStack fireballItem = FireballItem.createFireballItem();
        inventory.setItem(10, fireballItem);

        ItemStack throwableCreeperItem = ThrowableCreeperItem.createThrowableCreeperItem();
        inventory.setItem(16, throwableCreeperItem);

        return inventory;
    }
}
