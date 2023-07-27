package com.nate.elemental.utils.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.nate.elemental.Factions;
import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.UserTable;

public class PlayerDeathListener implements Listener {
    private final Database database;
    private final UserTable userTable;
    Factions plugin;

    public PlayerDeathListener(Database database) {
        this.database = database;
        this.userTable = new UserTable();
        this.plugin = Factions.getInstance();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        userTable.subtractPowerAndChunks(playerName, 3);

        int newPower = database.getPlayerPower(playerName);

        player.sendMessage("Your power is now " + newPower + "/10");

        schedulePowerIncrease(player);
    }

    private void schedulePowerIncrease(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            database.addPlayerPower(player.getName(), 1);
            int newPower = database.getPlayerPower(player.getName());
            player.sendMessage("Your power increased to " + newPower + "/10");

            if (newPower < 10) {
                schedulePowerIncrease(player);
            }
        }, 12000);
    }
}
