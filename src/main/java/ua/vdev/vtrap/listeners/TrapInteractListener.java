package ua.vdev.vtrap.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.models.factory.TrapFactory;
import ua.vdev.vtrap.managers.TrapManager;
import org.bukkit.NamespacedKey;

public class TrapInteractListener implements Listener {
    private final NamespacedKey trapKey = new NamespacedKey(VTrap.instance, "trap");
    private final TrapManager trapManager;

    public TrapInteractListener(TrapManager trapManager) {
        this.trapManager = trapManager;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(trapKey, PersistentDataType.STRING)) {
            return;
        }

        String trapId = item.getItemMeta().getPersistentDataContainer().get(trapKey, PersistentDataType.STRING);
        Trap trap = TrapFactory.create(trapId);
        if (trap == null) {
            e.setCancelled(true);
            return;
        }

        Player p = e.getPlayer();
        if (trapManager.canActivateTrap(p, trap)) {
            e.setCancelled(true);
            if (trapManager.activateTrap(p, trap)) {
                String matName = trap.itemInfo().material();
                if (matName != null) {
                    p.setCooldown(Material.valueOf(matName), trap.timing().cooldown() * 20);
                }
                p.playSound(p.getLocation(), Sound.valueOf(trap.sounds().sound()), SoundCategory.MASTER, 1.0f, 1.0f);
                item.setAmount(item.getAmount() - 1);
            }
        } else {
            e.setCancelled(true);
        }
    }
}