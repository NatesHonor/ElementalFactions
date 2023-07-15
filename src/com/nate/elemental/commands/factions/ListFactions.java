package com.nate.elemental.commands.factions;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;

public class ListFactions implements CommandExecutor {

    private Database database;
	@SuppressWarnings("unused")
	private Factions plugin;

    public ListFactions(Factions factions) {
    	this.plugin = factions;
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
