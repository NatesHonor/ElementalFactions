package com.nate.elemental;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nate.elemental.commands.data.h2.Debug;
import com.nate.elemental.commands.factions.AcceptCommand;
import com.nate.elemental.commands.factions.AllyCommand;
import com.nate.elemental.commands.factions.ClaimCommand;
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
import com.nate.elemental.commands.shops.ElixirCommand;
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
import com.nate.elemental.utils.storage.h2.FactionsTable;

import net.milkbowl.vault.economy.Economy;

public class Factions extends JavaPlugin implements Listener, CommandExecutor {
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
        GainPlayerPower gainPlayerPower = new GainPlayerPower(database, this);

        gainPlayerPower.enablePowerUpdates();
        FactionsTable.createTables();

        boolean canFireballExplode = true;

        HorseCommand horseCommand = new HorseCommand();
        ElixirCommand elixirCommand = new ElixirCommand(this);
        ClaimCommand claimCommand = new ClaimCommand(this);
        RaidShopCommand raidShopCommand = new RaidShopCommand(this);
        SpawnerShopCommand spawnerShopCommand = new SpawnerShopCommand(this);
        SpawnerPlaceListener spawnerPlaceListener = new SpawnerPlaceListener();
        SpawnerBreakListener spawnerBreakListener = new SpawnerBreakListener();
        FireballItem fireballItem = new FireballItem(this, canFireballExplode);
        CombatTagHandler combatTagHandler = new CombatTagHandler();
        PearlCooldownHandler pearlCooldownHandler = new PearlCooldownHandler(this);
        PlayerDeathListener playerDeathListener = new PlayerDeathListener(database, this);
        SettingsCommand settingsCommand = new SettingsCommand();
        SpawnerSpawnListener spawnerSpawnListener = new SpawnerSpawnListener();
        TrenchPickaxe trenchPickaxe = new TrenchPickaxe();

        getCommand("f").setExecutor(this);
        getCommand("horse").setExecutor(horseCommand);
        getCommand("elixir").setExecutor(elixirCommand);
        getCommand("debug-h2").setExecutor(new Debug());
        getCommand("raidshop").setExecutor(raidShopCommand);
        getCommand("spawnershop").setExecutor(spawnerShopCommand);

        getServer().getPluginManager().registerEvents(settingsCommand, this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(fireballItem, this);
        getServer().getPluginManager().registerEvents(claimCommand, this);
        getServer().getPluginManager().registerEvents(horseCommand, this);
        getServer().getPluginManager().registerEvents(raidShopCommand, this);
        getServer().getPluginManager().registerEvents(combatTagHandler, this);
        getServer().getPluginManager().registerEvents(spawnerShopCommand, this);
        getServer().getPluginManager().registerEvents(spawnerPlaceListener, this);
        getServer().getPluginManager().registerEvents(pearlCooldownHandler, this);
        getServer().getPluginManager().registerEvents(elixirCommand, this);
        getServer().getPluginManager().registerEvents(spawnerBreakListener, this);
        getServer().getPluginManager().registerEvents(playerDeathListener, this);
        getServer().getPluginManager().registerEvents(spawnerSpawnListener, this);

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

    private String getMessage(String key, String defaultValue) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, defaultValue));
    }

    public static String getConnectionURL() {
        if (url == null) {
            url = "jdbc:h2:" + Factions.instance.getDataFolder().getAbsolutePath() + "\\Factions";
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("f")) {
            if (args.length == 0) {
                String helpMessagePath = "Help-Message";
                if (messagesConfig.isConfigurationSection(helpMessagePath)) {
                    for (String key : messagesConfig.getConfigurationSection(helpMessagePath).getKeys(false)) {
                        String[] helpMessages = messagesConfig.getStringList(helpMessagePath + "." + key)
                                .toArray(new String[0]);
                        for (String helpMessage : helpMessages) {
                            sender.sendMessage(getMessage(key, helpMessage));

                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "No help message found for this command.");
                }
                return true;
            } else if (args.length >= 1) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "help":
                        String helpMessagePath = "Help-Message";
                        if (messagesConfig.isConfigurationSection(helpMessagePath)) {
                            for (String key : messagesConfig.getConfigurationSection(helpMessagePath).getKeys(false)) {
                                String[] helpMessages = messagesConfig.getStringList(helpMessagePath + "." + key)
                                        .toArray(new String[0]);
                                for (String helpMessage : helpMessages) {
                                    sender.sendMessage(getMessage(key, helpMessage));

                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "No help message found for this command.");
                        }
                        break;
                    case "ally":
                        if (args.length >= 2) {
                            AllyCommand allyCommand = new AllyCommand();
                            allyCommand.onCommand(sender, command, label, args);
                        } else {
                            sender.sendMessage(getMessage("usage.ally", "&cUsage: /f ally <faction>"));
                        }
                        break;
                    case "create":
                        if (args.length >= 2) {
                            CreateFactionCommand createCommand = new CreateFactionCommand();
                            createCommand.onCommand(sender, command, label, args);
                            return true;
                        } else {
                            sender.sendMessage(getMessage("usage.create", "&cUsage: /f create <name>"));
                        }
                        break;
                    case "desc":
                    case "description":
                        if (args.length >= 2) {
                            DescCommand descCommand = new DescCommand();
                            descCommand.onCommand(sender, command, label, args);
                            return true;
                        } else {
                            sender.sendMessage(getMessage("usage.desc", "&cUsage: /f desc <description>"));
                        }
                        break;
                    case "list":
                        if (args.length >= 1) {
                            ListFactions listCommand = new ListFactions(this);
                            listCommand.onCommand(sender, command, label, args);
                            return true;
                        }
                        break;
                    case "settings":
                        if (args.length >= 1) {
                            SettingsCommand settingsCommand = new SettingsCommand();
                            settingsCommand.onCommand(sender, command, label, args);
                        }
                        break;
                    case "show":
                        if (args.length >= 1) {
                            ShowCommand showFaction = new ShowCommand();
                            showFaction.onCommand(sender, command, label, args);
                        } else {
                            sender.sendMessage(getMessage("usage.show", "&cUsage: /f show"));
                        }
                        break;
                    case "disband":
                        if (args.length >= 1) {
                            DisbandCommand disbandCommand = new DisbandCommand(this);
                            disbandCommand.onCommand(sender, command, label, args);
                        } else {
                            sender.sendMessage(getMessage("usage.disband", "&cUsage: /f disband"));
                        }
                        break;
                    case "claim":
                        if (args.length >= 1) {
                            ClaimCommand claimCommand = new ClaimCommand(this);
                            claimCommand.onCommand(sender, command, label, args);
                        }
                        break;
                    case "map":
                        if (args.length >= 1) {
                            Database database = new Database();
                            MapCommand mapCommand = new MapCommand(database);
                            mapCommand.onCommand(sender, command, label, args);
                        }
                        break;
                    case "promote":
                        if (args.length >= 1) {
                            Database database = new Database();
                            PromoteCommand promoteCommand = new PromoteCommand(database);
                            promoteCommand.onCommand(sender, command, label, args);
                        } else {
                            sender.sendMessage(getMessage("usage.promote", "&cUsage: /f promote (user)"));
                        }
                        break;
                    case "accept":
                        AcceptCommand acceptCommand = new AcceptCommand();
                        acceptCommand.onCommand(sender, command, label, args);
                        break;
                    case "invite":
                        InviteCommand inviteCommand = new InviteCommand(this);
                        inviteCommand.onCommand(sender, command, label, args);
                        break;
                    default:
                        sender.sendMessage(getMessage("unknown-command", "&cUnknown command: " + subCommand));
                        break;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {

    }

}
