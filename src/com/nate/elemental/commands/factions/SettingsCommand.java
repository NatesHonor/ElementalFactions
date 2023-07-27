package com.nate.elemental.commands.factions;

import java.util.HashMap;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nate.elemental.utils.storage.h2.FactionUtils;

public class SettingsCommand implements CommandExecutor, Listener {
    private final Map<Player, String> selectedRank = new HashMap<>();
    private final Map<Player, String> prefixInput = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Faction Settings");

        ItemStack titlesItem = new ItemStack(Material.PAPER);
        ItemMeta titlesItemMeta = titlesItem.getItemMeta();
        titlesItemMeta.setDisplayName(ChatColor.GREEN + "Titles");
        titlesItem.setItemMeta(titlesItemMeta);

        inventory.setItem(0, titlesItem);
        player.openInventory(inventory);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = event.getView().getTitle();

        ItemStack clickedItem = event.getCurrentItem();

        if (inventoryTitle.equals(ChatColor.GOLD + "Faction Settings")) {
            event.setCancelled(true);

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.PAPER) {
                    openRankGUI(player);
                }
            }
        } else if (inventoryTitle.equals(ChatColor.GOLD + "Rank Selection")) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String selectedRank = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                this.selectedRank.put(player, selectedRank);
                player.closeInventory();
                player.sendMessage(
                        ChatColor.YELLOW + "Please enter what prefix you would like for " + selectedRank + ".");
                prefixInput.put(player, selectedRank);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (prefixInput.containsKey(player)) {
            event.setCancelled(true);
            String selectedRank = prefixInput.get(player);
            String prefix = event.getMessage();
            player.sendMessage(ChatColor.YELLOW + "Prefix for " + selectedRank + " saved!");
            storeRankPrefix(player, selectedRank, prefix);

            this.selectedRank.remove(player);
            this.prefixInput.remove(player);
        }
    }

    private void storeRankPrefix(Player player, String selectedRank, String prefix) {
        FactionUtils factionUtils = new FactionUtils();
        factionUtils.storeRankPrefix(player, selectedRank, prefix);
    }

    private void openRankGUI(Player player) {
        Inventory rankGUI = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Rank Selection");

        ItemStack leaderItem = new ItemStack(Material.PAPER);
        ItemMeta leaderItemMeta = leaderItem.getItemMeta();
        leaderItemMeta.setDisplayName(ChatColor.GOLD + "founder");
        leaderItem.setItemMeta(leaderItemMeta);

        ItemStack coLeaderItem = new ItemStack(Material.PAPER);
        ItemMeta coLeaderItemMeta = coLeaderItem.getItemMeta();
        coLeaderItemMeta.setDisplayName(ChatColor.GOLD + "CoLeader");
        coLeaderItem.setItemMeta(coLeaderItemMeta);

        ItemStack moderatorItem = new ItemStack(Material.PAPER);
        ItemMeta moderatorItemMeta = moderatorItem.getItemMeta();
        moderatorItemMeta.setDisplayName(ChatColor.GOLD + "Moderator");
        moderatorItem.setItemMeta(moderatorItemMeta);

        ItemStack trustedMemberItem = new ItemStack(Material.PAPER);
        ItemMeta trustedMemberItemMeta = trustedMemberItem.getItemMeta();
        trustedMemberItemMeta.setDisplayName(ChatColor.GOLD + "Trusted Member");
        trustedMemberItem.setItemMeta(trustedMemberItemMeta);

        ItemStack memberItem = new ItemStack(Material.PAPER);
        ItemMeta memberItemMeta = memberItem.getItemMeta();
        memberItemMeta.setDisplayName(ChatColor.GOLD + "Member");
        memberItem.setItemMeta(memberItemMeta);

        rankGUI.setItem(0, leaderItem);
        rankGUI.setItem(1, coLeaderItem);
        rankGUI.setItem(2, moderatorItem);
        rankGUI.setItem(3, trustedMemberItem);
        rankGUI.setItem(4, memberItem);

        player.openInventory(rankGUI);
    }
}
