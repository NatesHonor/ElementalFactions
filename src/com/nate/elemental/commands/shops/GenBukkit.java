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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GenBukkit implements CommandExecutor, Listener {

    private static final String MAIN_MENU_TITLE = "GenBukkit Options";
    private static final String VERTICAL_OPTION = "Vertical GenBukkits";
    private static final String HORIZONTAL_OPTION = "Horizontal GenBukkit";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        openGenBukkitMenu(player);

        return true;
    }

    private void openGenBukkitMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, MAIN_MENU_TITLE);

        ItemStack verticalOption = new ItemStack(Material.OAK_SIGN);
        ItemMeta verticalMeta = verticalOption.getItemMeta();
        verticalMeta.setDisplayName(ChatColor.YELLOW + VERTICAL_OPTION);
        verticalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click Obsidian GenBukkit",
                ChatColor.GRAY + "to place vertically."));
        verticalOption.setItemMeta(verticalMeta);

        ItemStack horizontalOption = new ItemStack(Material.SPRUCE_SIGN);
        ItemMeta horizontalMeta = horizontalOption.getItemMeta();
        horizontalMeta.setDisplayName(ChatColor.YELLOW + HORIZONTAL_OPTION);
        horizontalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click Obsidian GenBukkit",
                ChatColor.GRAY + "to place horizontally."));
        horizontalOption.setItemMeta(horizontalMeta);

        menu.setItem(3, verticalOption);
        menu.setItem(5, horizontalOption);

        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(MAIN_MENU_TITLE)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            if (clickedItem.getItemMeta() != null) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                if (displayName != null) {
                    if (displayName.equals(ChatColor.YELLOW + VERTICAL_OPTION)) {
                        player.setMetadata("genbukkitOption",
                                new org.bukkit.metadata.FixedMetadataValue(null, "vertical"));
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "Selected Vertical GenBukkits.");
                    } else if (displayName.equals(ChatColor.YELLOW + HORIZONTAL_OPTION)) {
                        player.setMetadata("genbukkitOption",
                                new org.bukkit.metadata.FixedMetadataValue(null, "horizontal"));
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "Selected Horizontal GenBukkit.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.OBSIDIAN && itemInHand.getItemMeta() != null
                    && itemInHand.getItemMeta().hasDisplayName()
                    && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Obsidian GenBukkit")) {

                if (player.hasMetadata("genbukkitOption")) {
                    String genbukkitOption = player.getMetadata("genbukkitOption").get(0).asString();
                    if (genbukkitOption.equals("vertical")) {
                        Location clickedLocation = event.getClickedBlock().getLocation();
                        for (int y = clickedLocation.getBlockY(); y <= clickedLocation.getWorld().getMaxHeight(); y++) {
                            Location targetLocation = new Location(clickedLocation.getWorld(),
                                    clickedLocation.getBlockX(), y, clickedLocation.getBlockZ());
                            if (targetLocation.getBlock().getType() == Material.AIR) {
                                targetLocation.getBlock().setType(Material.OBSIDIAN);
                            } else {
                                break;
                            }
                        }
                    } else if (genbukkitOption.equals("horizontal")) {
                        // Place obsidian block horizontally from the clicked block
                        // (Implement the horizontal logic here)
                        player.sendMessage(ChatColor.RED + "Horizontal GenBukkit is not implemented yet!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You must select a GenBukkit option first using /genbukkit.");
                }
            }
        }
    }
}
