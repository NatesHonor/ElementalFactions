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

import com.nate.elemental.Factions;

import java.util.Arrays;

public class GenBukkit implements CommandExecutor, Listener {

    private static final String MAIN_MENU_TITLE = "GenBukkit Options";
    private static final String VERTICAL_OPTION = "Vertical GenBukkits";
    private static final String HORIZONTAL_OPTION = "Horizontal GenBukkit";

    private final Factions plugin;

    public GenBukkit() {
        this.plugin = Factions.getInstance();
    }

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
        verticalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click Lava Bucket GenBukkit",
                ChatColor.GRAY + "to place vertically."));
        verticalOption.setItemMeta(verticalMeta);

        ItemStack horizontalOption = new ItemStack(Material.SPRUCE_SIGN);
        ItemMeta horizontalMeta = horizontalOption.getItemMeta();
        horizontalMeta.setDisplayName(ChatColor.YELLOW + HORIZONTAL_OPTION);
        horizontalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click Lava Bucket GenBukkit",
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
                                new org.bukkit.metadata.FixedMetadataValue(plugin, "vertical"));
                        openVerticalGenBukkitObtainMenu(player);
                    } else if (displayName.equals(ChatColor.YELLOW + HORIZONTAL_OPTION)) {
                        player.setMetadata("genbukkitOption",
                                new org.bukkit.metadata.FixedMetadataValue(plugin, "horizontal"));
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
            if (itemInHand.getType() == Material.LAVA_BUCKET && itemInHand.getItemMeta() != null
                    && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lava Bucket GenBukkit")) {

                if (player.hasMetadata("genbukkitOption")) {
                    String genbukkitOption = player.getMetadata("genbukkitOption").get(0).asString();
                    if (genbukkitOption.equals("vertical")) {
                        Location clickedLocation = event.getClickedBlock().getLocation();
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> generateVerticalObsidian(clickedLocation), 0L,
                                20L);
                    } else if (genbukkitOption.equals("horizontal")) {
                        player.sendMessage(ChatColor.RED + "Horizontal GenBukkit is not implemented yet!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You must select a GenBukkit option first using /genbukkit.");
                }
            }
        }
    }

    private void openVerticalGenBukkitObtainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "Vertical GenBukkit");

        ItemStack lavaBucketGenBukkitItem = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta lavaBucketGenBukkitMeta = lavaBucketGenBukkitItem.getItemMeta();
        lavaBucketGenBukkitMeta.setDisplayName(ChatColor.YELLOW + "Lava Bucket GenBukkit");
        lavaBucketGenBukkitItem.setItemMeta(lavaBucketGenBukkitMeta);

        menu.setItem(4, lavaBucketGenBukkitItem);

        player.openInventory(menu);
    }

    private void generateVerticalObsidian(Location location) {
        int currentY = location.getBlockY();
        int maxHeight = location.getWorld().getMaxHeight();

        if (currentY > maxHeight) {
            return;
        }

        Location targetLocation = new Location(location.getWorld(), location.getBlockX(), currentY,
                location.getBlockZ());
        if (targetLocation.getBlock().getType() == Material.AIR) {
            targetLocation.getBlock().setType(Material.OBSIDIAN);
        }

        currentY++;
    }
}
