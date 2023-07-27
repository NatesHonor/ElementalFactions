package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.FactionUtils;

import java.util.Arrays;

public class DescCommand implements CommandExecutor, Listener {
    private final Database database;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    public DescCommand() {
        this.database = new Database();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /desc <description>");
            return true;
        }

        String[] descriptionArgs = Arrays.copyOfRange(args, 1, args.length);
        String description = String.join(" ", descriptionArgs);
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            player.sendMessage(
                    ChatColor.RED + "The faction description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
            return true;
        }

        String factionName = database.getUserFactionName(player.getName());

        FactionUtils.changeFactionDescription(factionName, description);

        player.sendMessage(ChatColor.GREEN + "Faction description updated successfully!");

        return true;
    }
}
