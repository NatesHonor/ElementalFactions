package com.nate.elemental.commands.quests;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Quest {
    private String name;
    private List<String> lore; // Change meta to List<String> for the lore
    private ItemStack itemReward;
    private double moneyReward;
    private int expReward;

    public Quest(String name, List<String> lore, ItemStack itemReward, double moneyReward, int expReward) {
        this.name = name;
        this.lore = lore;
        this.itemReward = itemReward;
        this.moneyReward = moneyReward;
        this.expReward = expReward;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() { // Add getLore method to retrieve the lore
        return lore;
    }

    public ItemStack getItemReward() {
        return itemReward;
    }

    public double getMoneyReward() {
        return moneyReward;
    }

    public int getExpReward() {
        return expReward;
    }
}
