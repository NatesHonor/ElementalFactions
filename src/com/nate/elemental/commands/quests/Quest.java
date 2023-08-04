package com.nate.elemental.commands.quests;

import org.bukkit.inventory.ItemStack;

public class Quest {
    private String name;
    private String meta;
    private ItemStack itemReward;
    private double moneyReward;
    private int expReward;

    public Quest(String name, String meta, ItemStack itemReward, double moneyReward, int expReward) {
        this.name = name;
        this.meta = meta;
        this.itemReward = itemReward;
        this.moneyReward = moneyReward;
        this.expReward = expReward;
    }

    public String getName() {
        return name;
    }

    public String getMeta() {
        return meta;
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
