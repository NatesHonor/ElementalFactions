package com.nate.elemental.commands.factions;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nate.elemental.utils.storage.h2.Database;

public class ListFactions implements CommandExecutor, Listener {

    private Database database;

    public ListFactions() {
        this.database = new Database();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<String> factions = database.getFactions();
            player.sendMessage("Factions: " + String.join(", ", factions));
        }

        return true;
    }
}
