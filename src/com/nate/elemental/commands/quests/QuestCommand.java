package com.nate.elemental.commands.quests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class QuestCommand implements CommandExecutor {
    QuestManager questManager = new QuestManager();

    public QuestCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("quest")) {
            if (args.length == 0) {
                displayQuestList(sender);
                return true;
            } else if (args.length == 1) {
                String questName = args[0];
                Quest quest = questManager.getQuestByName(questName);
                if (quest != null) {
                    displayQuestInfo(sender, quest);
                } else {
                    sender.sendMessage(ChatColor.RED + "Quest not found: " + questName);
                }
                return true;
            }
        }
        return false;
    }

    private void displayQuestList(CommandSender sender) {
        List<Quest> quests = questManager.getQuests();
        if (!quests.isEmpty()) {
            sender.sendMessage(ChatColor.BOLD + "Quests List:");
            for (Quest quest : quests) {
                sender.sendMessage(ChatColor.YELLOW + " - " + quest.getName());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "No quests available.");
        }
    }

    private void displayQuestInfo(CommandSender sender, Quest quest) {
        sender.sendMessage(ChatColor.BOLD + "Quest: " + quest.getName());
        sender.sendMessage(ChatColor.YELLOW + "Meta: " + quest.getMeta());
        sender.sendMessage(ChatColor.YELLOW + "Rewards:");
        if (quest.getItemReward() != null) {
            sender.sendMessage(ChatColor.YELLOW + "  - Item Reward: " + quest.getItemReward().getType());
        }
        if (quest.getMoneyReward() > 0) {
            sender.sendMessage(ChatColor.YELLOW + "  - Money Reward: " + quest.getMoneyReward());
        }
        if (quest.getExpReward() > 0) {
            sender.sendMessage(ChatColor.YELLOW + "  - Exp Reward: " + quest.getExpReward());
        }
    }
}
