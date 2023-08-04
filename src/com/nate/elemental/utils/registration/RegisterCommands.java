package com.nate.elemental.utils.registration;

import org.bukkit.plugin.java.JavaPlugin;

import com.nate.elemental.commands.Main;
import com.nate.elemental.commands.data.h2.Debug;
import com.nate.elemental.commands.items.TrenchPickaxe;
import com.nate.elemental.commands.quests.QuestCommand;
import com.nate.elemental.commands.shops.ElixirCommand;
import com.nate.elemental.commands.shops.GenBukkit;
import com.nate.elemental.commands.shops.HorseCommand;
import com.nate.elemental.commands.shops.RaidShopCommand;
import com.nate.elemental.commands.shops.SpawnerShopCommand;

public class RegisterCommands {

    public static void register(JavaPlugin plugin) {
        HorseCommand horseCommand = new HorseCommand();
        ElixirCommand elixirCommand = new ElixirCommand();
        RaidShopCommand raidShopCommand = new RaidShopCommand();
        SpawnerShopCommand spawnerShopCommand = new SpawnerShopCommand(plugin);
        TrenchPickaxe trenchPickaxe = new TrenchPickaxe();
        GenBukkit genBukkit = new GenBukkit();
        Main main = new Main();
        QuestCommand quest = new QuestCommand();

        plugin.getCommand("guild").setExecutor(main);
        plugin.getCommand("quest").setExecutor(quest);
        plugin.getCommand("f").setExecutor(main);
        plugin.getCommand("horse").setExecutor(horseCommand);
        plugin.getCommand("elixir").setExecutor(elixirCommand);
        plugin.getCommand("debug-h2").setExecutor(new Debug());
        plugin.getCommand("raidshop").setExecutor(raidShopCommand);
        plugin.getCommand("spawnershop").setExecutor(spawnerShopCommand);
        plugin.getCommand("trenchpickaxe").setExecutor(trenchPickaxe);
        plugin.getCommand("genbukkit").setExecutor(genBukkit);

    }

}
