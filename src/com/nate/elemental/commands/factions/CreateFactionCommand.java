package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionsTable;

public class CreateFactionCommand implements CommandExecutor, Listener {

    private Database database;

    public CreateFactionCommand() {
        this.database = new Database();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Invalid command format. Usage: /f create <name>");
                return true;
            }

            String factionName = args[1];

            if (FactionsTable.factionExists(factionName)) {
                player.sendMessage("A faction with that name already exists.");
                return true;
            }

            if (this.database.playerExists(player.getName())) {
                String currentFaction = this.database.getUserFactionName(player.getName());
                if (!currentFaction.equalsIgnoreCase("wilderness")) {
                    player.sendMessage("In order to create a new faction, you have to leave your current one!");
                    return true;
                }
            }

            String description = "A regular faction";
            int power = database.getusersPower(player.getName());
            int chunks = power;

            database.createFaction(factionName, player.getName(), description, power, chunks);
            database.updateusersFaction(player.getName(), factionName, "founder");

            Bukkit.broadcastMessage(player.getName() + " created the faction " + factionName + ".");
        }

        return true;
    }
}
