package ua.tox8729.vtrap.listeners;

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
import ua.tox8729.vtrap.VTrap;
import ua.tox8729.vtrap.models.Trap;
import ua.tox8729.vtrap.managers.TrapManager;
import org.bukkit.NamespacedKey;

public class TrapInteractListener implements Listener {
    private final NamespacedKey trapKey = new NamespacedKey(VTrap.instance, "trap");
    private final TrapManager trapManager;

    public TrapInteractListener(TrapManager trapManager) {
        this.trapManager = trapManager;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(trapKey, PersistentDataType.STRING)) {
            return;
        }

        String trapId = item.getItemMeta().getPersistentDataContainer().get(trapKey, PersistentDataType.STRING);
        Trap trap = Trap.fromConfig(trapId);
        if (trap == null) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (trapManager.canActivateTrap(player, trap)) {
            event.setCancelled(true);
            if (trapManager.activateTrap(player, trap)) {
                String materialName = VTrap.instance.getConfig().getString("traps." + trapId + ".material");
                if (materialName != null) {
                    player.setCooldown(Material.valueOf(materialName), trap.getCooldown() * 20);
                }
                player.playSound(player.getLocation(), Sound.valueOf(trap.getSound()), SoundCategory.MASTER, 1.0f, 1.0f);
                item.setAmount(item.getAmount() - 1);
            }
        } else {
            event.setCancelled(true);
        }
    }
}