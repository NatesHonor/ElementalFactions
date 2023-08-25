package com.nate.elemental.commands.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuestCommand implements CommandExecutor, Listener {
    private QuestManager questManager = new QuestManager();
    private Map<Player, Quest> claimedQuests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("quest")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("quests.reload")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }

                player.sendMessage(ChatColor.YELLOW + "Refreshing quests.yml...");
                questManager.reloadQuests();
                player.sendMessage(ChatColor.GREEN + "Quests reloaded successfully.");
                return true;
            } else {
                openQuestGUI(player);
                return true;
            }
        }

        return false;
    }

    private void openQuestGUI(Player player) {
        List<Quest> quests = questManager.getQuests();

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.BOLD + "Quests List");
        for (int i = 0; i < quests.size(); i++) {
            Quest quest = quests.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + quest.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Description:");
            lore.addAll(quest.getLore());

            lore.add(ChatColor.GREEN + "Reward:");
            if (quest.getItemReward() != null) {
                lore.add(ChatColor.GOLD + " - " + quest.getItemReward().getAmount() + " "
                        + quest.getItemReward().getType().toString());
            }
            if (quest.getMoneyReward() > 0) {
                lore.add(ChatColor.GOLD + " - " + quest.getMoneyReward() + " coins");
            }
            if (quest.getExpReward() > 0) {
                lore.add(ChatColor.GOLD + " - " + quest.getExpReward() + " exp");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(i, item);
        }

        ItemStack redWool = new ItemStack(Material.RED_WOOL);
        ItemMeta redWoolMeta = redWool.getItemMeta();
        redWoolMeta.setDisplayName(ChatColor.RED + "Placeholder");
        redWool.setItemMeta(redWoolMeta);
        int[] centerSlots = { 47, 48, 49 };
        for (int slot : centerSlots) {
            gui.setItem(slot, redWool);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(player.getOpenInventory().getTopInventory())) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String questName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                Quest quest = questManager.getQuestByName(questName);
                if (quest != null) {
                    if (claimedQuests.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "You have already claimed a quest. Complete it first.");
                        return;
                    }

                    if (claimedQuests.size() >= 3) {
                        player.sendMessage(ChatColor.RED + "You can only claim up to 3 quests at once.");
                        return;
                    }

                    player.sendMessage(ChatColor.GREEN + "You claimed the quest: " + quest.getName());

                    if (quest.getItemReward() != null) {
                        player.getInventory().addItem(quest.getItemReward());
                    }
                    if (quest.getMoneyReward() > 0) {
                        player.sendMessage(ChatColor.YELLOW + "You received " + quest.getMoneyReward() + " coins.");
                    }
                    if (quest.getExpReward() > 0) {
                        player.giveExp(quest.getExpReward());
                    }

                    int[] centerSlots = { 48, 49, 50 };
                    for (int slot : centerSlots) {
                        if (player.getOpenInventory().getItem(slot) == null) {
                            player.getOpenInventory().setItem(slot, clickedItem);
                            break;
                        }
                    }

                    claimedQuests.put(player, quest);
                } else {
                    player.sendMessage(ChatColor.RED + "Quest not found: " + questName);
                }
            }
        }
    }
}
