package com.nate.elemental.commands.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.nate.elemental.Factions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private List<Quest> quests;
    Factions factions = Factions.getInstance();

    public QuestManager() {
        this.quests = new ArrayList<>();
        loadQuests();
    }

    private void loadQuests() {
        createQuestsFile();
        File questsFile = new File(factions.getDataFolder(), "quests.yml");
        if (questsFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(questsFile);
            if (config.isConfigurationSection("quests")) {
                ConfigurationSection questsSection = config.getConfigurationSection("quests");
                for (String questName : questsSection.getKeys(false)) {
                    ConfigurationSection questSection = questsSection.getConfigurationSection(questName);
                    String meta = questSection.getString("meta");
                    ItemStack itemReward = questSection.getItemStack("itemReward");
                    double moneyReward = questSection.getDouble("moneyReward");
                    int expReward = questSection.getInt("expReward");
                    Quest quest = new Quest(questName, meta, itemReward, moneyReward, expReward);
                    quests.add(quest);
                }
            }
        }
    }

    private void createQuestsFile() {
        File questsFile = new File(factions.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            factions.saveResource("quests.yml", false);
        }
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public Quest getQuestByName(String name) {
        for (Quest quest : quests) {
            if (quest.getName().equalsIgnoreCase(name)) {
                return quest;
            }
        }
        return null;
    }
}
