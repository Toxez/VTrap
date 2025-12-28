package ua.vdev.vtrap.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.models.effects.TrapEffect;

import java.util.List;

public class TrapEffectManager {

    public void applyEffects(Player activator, Trap trap, ProtectedCuboidRegion region) {
        if (trap.effects().isEmpty()) return;

        for (TrapEffect trapEffect : trap.effects()) {
            switch (trapEffect.target()) {
                case ACTIVATOR -> applyToPlayer(activator, trapEffect.effects());
                case VICTIMS -> applyToRegionVictims(activator, region, trapEffect.effects());
            }
        }
    }

    private void applyToPlayer(Player player, List<PotionEffect> effects) {
        if (player != null && player.isOnline()) {
            player.addPotionEffects(effects);
        }
    }

    private void applyToRegionVictims(Player activator, ProtectedCuboidRegion region, List<PotionEffect> effects) {
        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
            if (targetPlayer.getUniqueId().equals(activator.getUniqueId())) continue;

            if (region.contains(BukkitAdapter.asBlockVector(targetPlayer.getLocation()))) {
                targetPlayer.addPotionEffects(effects);
            }
        }
    }
}