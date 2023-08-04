package com.nate.elemental.utils.registration;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.nate.elemental.commands.factions.CreateFactionCommand;
import com.nate.elemental.commands.factions.DescCommand;
import com.nate.elemental.commands.factions.DisbandCommand;
import com.nate.elemental.commands.factions.InviteCommand;
import com.nate.elemental.commands.factions.ListFactions;
import com.nate.elemental.commands.factions.MapCommand;
import com.nate.elemental.commands.factions.PromoteCommand;
import com.nate.elemental.commands.factions.SettingsCommand;
import com.nate.elemental.commands.factions.ShowCommand;
import com.nate.elemental.commands.items.TrenchPickaxe;
import com.nate.elemental.commands.quests.QuestCommand;
import com.nate.elemental.commands.shops.ElixirCommand;
import com.nate.elemental.commands.shops.GenBukkit;
import com.nate.elemental.commands.shops.HorseCommand;
import com.nate.elemental.commands.shops.RaidShopCommand;
import com.nate.elemental.commands.shops.SpawnerShopCommand;
import com.nate.elemental.items.FireballItem;
import com.nate.elemental.utils.CombatTagHandler;
import com.nate.elemental.utils.PearlCooldownHandler;
import com.nate.elemental.utils.events.GainPlayerPower;
import com.nate.elemental.utils.events.PlayerDeathListener;
import com.nate.elemental.utils.shops.spawner.SpawnerBreakListener;
import com.nate.elemental.utils.shops.spawner.SpawnerPlaceListener;
import com.nate.elemental.utils.shops.spawner.SpawnerSpawnListener;
import com.nate.elemental.utils.storage.h2.Database;

public class RegisterEvents {
    public static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new HorseCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new ElixirCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new RaidShopCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new SpawnerShopCommand(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new TrenchPickaxe(), plugin);
        Bukkit.getPluginManager().registerEvents(new GenBukkit(), plugin);
        Bukkit.getPluginManager().registerEvents(new FireballItem(plugin, true), plugin);
        Bukkit.getPluginManager().registerEvents(new SettingsCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new CombatTagHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new PearlCooldownHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(new Database()), plugin);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GainPlayerPower(new Database()), plugin);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CreateFactionCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new DescCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new DisbandCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new InviteCommand(), plugin);
        Bukkit.getPluginManager().registerEvents(new ListFactions(), plugin);
        Bukkit.getPluginManager().registerEvents(new MapCommand(), plugin);
        registerEvent(new PromoteCommand(new Database()), plugin);
        registerEvent(new ShowCommand(), plugin);

        registerEvent(new QuestCommand(), plugin);
    }

    private static void registerEvent(Listener string, JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(string, plugin);
    }
}
