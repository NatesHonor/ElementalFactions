package com.nate.elemental.commands.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import com.nate.elemental.utils.shops.elixir.ElixirConfig;
import com.nate.elemental.utils.shops.elixir.ElixirConfig.Elixir;

public class ElixirCommand implements CommandExecutor, Listener {
    private final ElixirConfig elixirConfig;

    public ElixirCommand() {
        this.elixirConfig = new ElixirConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        openElixirGUI(player);
        return true;
    }

    private void openElixirGUI(Player player) {
        Inventory inventory = createElixirGUI();
        player.openInventory(inventory);
    }

    private Inventory createElixirGUI() {
        Inventory inventory = Bukkit.createInventory(new ElixirInventoryHolder(), 27,
                ChatColor.BOLD + "Elixir Selection");

        List<Elixir> elixirs = elixirConfig.getElixirs();
        for (Elixir elixir : elixirs) {
            addCustomElixir(inventory, Material.POTION, elixir.getName(), elixir.getEffects());
        }

        return inventory;
    }

    private void addCustomElixir(Inventory inventory, Material material, String name, List<PotionEffect> effects) {
        ItemStack elixir = new ItemStack(material);
        PotionMeta potionMeta = (PotionMeta) elixir.getItemMeta();

        for (PotionEffect effect : effects) {
            potionMeta.addCustomEffect(effect, true);
        }

        elixir.setItemMeta(potionMeta);

        ItemMeta itemMeta = elixir.getItemMeta();
        itemMeta.setDisplayName(name);
        elixir.setItemMeta(itemMeta);

        inventory.addItem(elixir);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof ElixirInventoryHolder) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem();
                Player player = (Player) event.getWhoClicked();

                if (clickedItem.hasItemMeta() && clickedItem.getItemMeta() instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) clickedItem.getItemMeta();
                    List<PotionEffect> effects = new ArrayList<>(potionMeta.getCustomEffects());
                    if (effects != null && !effects.isEmpty()) {
                        ItemStack potionBottle = new ItemStack(Material.POTION);

                        PotionMeta potionBottleMeta = (PotionMeta) potionBottle.getItemMeta();
                        for (PotionEffect effect : effects) {
                            potionBottleMeta.addCustomEffect(effect, true);
                        }
                        potionBottleMeta.setDisplayName(potionMeta.getDisplayName());
                        potionBottle.setItemMeta(potionBottleMeta);

                        player.getInventory().addItem(potionBottle);

                        player.sendMessage(ChatColor.GREEN + "You have obtained an elixir!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack consumedItem = event.getItem();
        if (consumedItem != null && consumedItem.getType() == Material.POTION && consumedItem.hasItemMeta() &&
                consumedItem.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) consumedItem.getItemMeta();
            List<PotionEffect> effects = new ArrayList<>(potionMeta.getCustomEffects());
            if (effects != null && !effects.isEmpty()) {
                Player player = event.getPlayer();
                for (PotionEffect effect : effects) {
                    player.addPotionEffect(effect, true);
                }
                player.sendMessage(ChatColor.GREEN + "You have obtained an elixir!");
            }
        }
    }

    private static class ElixirInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
