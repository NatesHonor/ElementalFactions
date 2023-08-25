package com.nate.elemental;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nate.elemental.utils.events.GainPlayerPower;
import com.nate.elemental.utils.registration.RegisterCommands;
import com.nate.elemental.utils.registration.RegisterEvents;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionsTable;

import net.milkbowl.vault.economy.Economy;

public class Factions extends JavaPlugin implements Listener {
    private static String url;
    private static Factions instance;
    private Economy economy;
    boolean usePackets = false;
    private static FileConfiguration messagesConfig;
    private File configFile;

    public static Factions getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        url = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "\\Factions";

        try {
            Class.forName("org.h2.Driver");
            DriverManager.getConnection(url);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        if (!setupEconomy()) {
            getLogger().warning("Vault dependency not found. Economy integration disabled.");
        }

        Database database = new Database();

        GainPlayerPower gainPlayerPower = new GainPlayerPower(database);

        gainPlayerPower.enablePowerUpdates();
        FactionsTable.createTables();

        RegisterEvents.register(this);
        RegisterCommands.register(this);
        // new File(getDataFolder(), "messages.yml"); just incase I need this later and
        // the below is deleted/moved
        configFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists()) {
            saveResource("messages.yml", false);
        }

        if (messagesConfig == null) {
            getLogger().severe("messagesConfig is null!");
        } else {
            getLogger().info("Messages.yml loaded successfully.");
        }
        String helpMessagePath = "Help-Message";
        getLogger().info("Help message section exists: " + messagesConfig.isConfigurationSection(helpMessagePath));
    }

    @EventHandler
    public void createPlayerOnJoin(PlayerJoinEvent e) {
        Database database = new Database();
        Player player = e.getPlayer();
        String playerName = player.getName();

        if (!database.playerExists(playerName)) {
            database.addPlayer(playerName, "wilderness", 10, 10);
            Bukkit.getLogger().info(playerName + ": wilderness power: 10 chunks: 10");
        }
    }

    public static String getConnectionURL() {
        if (url == null) {
            url = "jdbc:h2:" + Factions.instance.getDataFolder().getAbsolutePath() + "\\Factions;TRACE_LEVEL_FILE=0";
            ;
        }
        return url;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    @Override
    public void onDisable() {

    }

}
