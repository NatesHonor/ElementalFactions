package com.nate.elemental.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.InvitesTable;

public class InviteCommand implements CommandExecutor {
    private final Factions plugin;
    private final Database database;
    private final InvitesTable invitesTable;
    public InviteCommand(Factions plugin) {
        this.plugin = plugin;
        this.database = new Database();
        this.invitesTable = new InvitesTable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player inviter = (Player) sender;

        if (args.length < 2) {
            inviter.sendMessage(ChatColor.RED + "Usage: /f invite <playername>");
            return true;
        }

        String inviteeName = args[1];
        Player invitee = Bukkit.getPlayer(inviteeName);

        if (invitee == null) {
            inviter.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        String factionName = database.getUserFactionName(inviter.getName());

        if (factionName.equals("wilderness") || factionName.equals("warzone") || factionName.equals("safezone")) {
            inviter.sendMessage(ChatColor.RED + "You are not currently in a faction.");
            return true;
        }

        if (invitesTable.isInvitePending(invitee.getName(), inviter.getName())) {
            inviter.sendMessage(ChatColor.RED + "An invitation has already been sent to that player.");
            return true;
        }

        long expiryTime = System.currentTimeMillis() + (30 * 1000);
        invitesTable.addInvite(inviter.getName(), invitee.getName(), factionName, expiryTime);

        String inviteMessage = ChatColor.YELLOW + inviter.getName() + " invited you to the faction " + factionName
                + ". Type '/f accept " + inviter.getName() + "' to accept!";
        invitee.sendMessage(inviteMessage);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (invitesTable.isInvitePending(invitee.getName(), inviter.getName())) {
            	invitesTable.removeInvite(invitee.getName(), inviter.getName());
                invitee.sendMessage(ChatColor.RED + "The faction invitation has expired.");
            }
        }, 30 * 20);

        return true;
    }
}
