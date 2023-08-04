package com.nate.elemental.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
import com.nate.elemental.utils.storage.h2.Database;

public class Main implements CommandExecutor {
    private static FileConfiguration messagesConfig;
    private File configFile;

    private String getMessage(String key, String defaultValue) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, defaultValue));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("guild") || command.getName().equalsIgnoreCase("f")) {
            if (args.length == 0) {
                messagesConfig = YamlConfiguration.loadConfiguration(configFile);
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
                            ListFactions listCommand = new ListFactions();
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
                            DisbandCommand disbandCommand = new DisbandCommand();
                            disbandCommand.onCommand(sender, command, label, args);
                        } else {
                            sender.sendMessage(getMessage("usage.disband", "&cUsage: /f disband"));
                        }
                        break;
                    case "claim":
                        if (args.length >= 1) {
                            ClaimCommand claimCommand = new ClaimCommand();
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
                        InviteCommand inviteCommand = new InviteCommand();
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

}
