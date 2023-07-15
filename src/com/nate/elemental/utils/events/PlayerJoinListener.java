package com.nate.elemental.utils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.nate.elemental.utils.storage.h2.Database;

public class PlayerJoinListener implements Listener {

    private Database database;

    public PlayerJoinListener(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!database.playerExists(player.getName())) {
            database.addPlayer(player.getName(), "wilderness", 10, 10);
        }
    }
}
