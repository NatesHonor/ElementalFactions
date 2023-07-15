package com.nate.elemental.commands.shops;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SpawnerShopCommand implements CommandExecutor, Listener {

    private final int pageSize = 27;
    private final List<EntityType> spawnerTypes;
    private static SpawnerShopCommand instance;
    
    public SpawnerShopCommand(Plugin plugin) {
        this.spawnerTypes = Arrays.asList(
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.PIG,
                EntityType.COW,
                EntityType.CREEPER,
                EntityType.BLAZE
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        Inventory shopInventory = createSpawnerShopInventory(0);
        player.openInventory(shopInventory);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryTitle = event.getView().getTitle();
        if (inventoryTitle.equals("Spawner Shop")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            if (clickedItem.getType() == Material.ARROW) {
                int currentPage = getPageFromInventory(inventoryTitle);
                Inventory nextPageInventory = createSpawnerShopInventory(currentPage + 1);
                player.openInventory(nextPageInventory);
            } else if (clickedItem.getType() == Material.SPAWNER) {
                EntityType entityType = getEntityTypeFromSpawnerItem(clickedItem);
                if (entityType != null) {
                    giveSpawnerToPlayer(player, entityType);
                    openEmptySpawnerGUI(player);
                }
            }
        }
    }

    private void giveSpawnerToPlayer(Player player, EntityType entityType) {
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        BlockStateMeta spawnerMeta = (BlockStateMeta) spawnerItem.getItemMeta();
        BlockState blockState = spawnerMeta.getBlockState();

        if (blockState instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
            creatureSpawner.setSpawnedType(entityType);
            blockState.update();
        }

        spawnerMeta.setBlockState(blockState);
        spawnerMeta.setDisplayName(ChatColor.YELLOW + capitalizeEntityName(entityType) + " Spawner");
        spawnerItem.setItemMeta(spawnerMeta);

        player.getInventory().addItem(spawnerItem);

        player.sendMessage(ChatColor.GREEN + "You purchased a " + capitalizeEntityName(entityType) + " spawner!");
    }

    private String capitalizeEntityName(EntityType entityType) {
        String entityName = entityType.getName();
        String[] nameParts = entityName.split("_");
        StringBuilder capitalized = new StringBuilder();
        for (String part : nameParts) {
            capitalized.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return capitalized.toString().trim();
    }


    private void openEmptySpawnerGUI(Player player) {
    }

    public static SpawnerShopCommand getInstance() {
        return instance;
    }
    
    public static void setInstance(SpawnerShopCommand instance) {
        SpawnerShopCommand.instance = instance;
    }
    
    private Inventory createSpawnerShopInventory(int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, spawnerTypes.size());

        int inventorySize = ((int) Math.ceil((double) (endIndex - startIndex) / 9) + 1) * 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Spawner Shop");

        for (int i = startIndex, slot = 0; i < endIndex; i++, slot++) {
            if (slot % 9 < 9) {
                EntityType entityType = spawnerTypes.get(i);
                ItemStack spawnerItem = createSpawnerItem(entityType);
                inventory.setItem(slot, spawnerItem);
            }
        }

        if (endIndex < spawnerTypes.size()) {
            ItemStack nextPageArrow = createNextPageArrow(page);
            inventory.setItem(inventorySize - 1, nextPageArrow);
        }

        return inventory;
    }

    private ItemStack createSpawnerItem(EntityType entityType) {
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawnerItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + capitalizeEntityName(entityType) + " Spawner");
        spawnerItem.setItemMeta(meta);
        return spawnerItem;
    }

    private ItemStack createNextPageArrow(int currentPage) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Next Page");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Page: " + (currentPage + 1)));
        arrow.setItemMeta(meta);
        return arrow;
    }

    private EntityType getEntityTypeFromSpawnerItem(ItemStack itemStack) {
        if (itemStack.getType() == Material.SPAWNER) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            if (displayName != null && displayName.endsWith(" Spawner")) {
                String entityName = displayName.substring(2, displayName.indexOf(" Spawner"));
                return EntityType.fromName(entityName);
            }
        }
        return null;
    }

    private int getPageFromInventory(String inventoryTitle) {
        String pageNumber = inventoryTitle.substring(inventoryTitle.lastIndexOf("Page: ") + 6);
        return Integer.parseInt(pageNumber) - 1;
    }
}
