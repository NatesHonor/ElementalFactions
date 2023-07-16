package com.nate.elemental.commands.factions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.InvitesTable;

public class AcceptCommand implements CommandExecutor {
    private final Database database;
    private final InvitesTable invitesTable;
    public AcceptCommand() {
        this.database = new Database();
        this.invitesTable = new InvitesTable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /f accept <inviter>");
            return true;
        }

        String inviterName = args[1];

        if (!invitesTable.isInvitePending(inviterName, player.getName())) {
            player.sendMessage(ChatColor.RED + "You don't have a pending invitation from that player.");
            return true;
        }

        String factionName = database.getInviterFactionName(player.getName(), inviterName);
        database.acceptFactionInvitation(player.getName(), inviterName);
        player.sendMessage(ChatColor.GREEN + "You have accepted the faction invitation from " + inviterName + ".");
        player.sendMessage(ChatColor.GREEN + "You are now a member of the faction " + factionName + ".");

        return true;
    }
}
