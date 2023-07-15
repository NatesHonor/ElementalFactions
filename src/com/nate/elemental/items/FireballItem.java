package com.nate.elemental.items;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class FireballItem implements Listener {
    private final boolean canFireballExplode;

    public FireballItem(Plugin plugin, boolean canFireballExplode) {
        this.canFireballExplode = canFireballExplode;
    }

    public static ItemStack createFireballItem() {
        ItemStack itemStack = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = itemStack.getItemMeta();
        
        String displayName = "§6✷ §eFireball §6✷";
        meta.setDisplayName(displayName);
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    public static void giveFireball(Player player) {
        ItemStack fireball = createFireballItem();
        player.getInventory().addItem(fireball);
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.FIRE_CHARGE) {
            if (event.getAction().name().contains("RIGHT")) {
                ItemStack fireballItem = player.getInventory().getItemInMainHand();
                if (fireballItem.getAmount() > 1) {
                    fireballItem.setAmount(fireballItem.getAmount() - 1);
                } else {
                    player.getInventory().remove(fireballItem);
                }

                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setYield(canFireballExplode ? 2.5F : 2.5F);
            }
        }
    }
}
