package ua.vdev.vtrap.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.utils.schematic.SchematicUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrapManager {
    private final Map<UUID, Map<String, Long>> playerCooldowns = new HashMap<>();
    private final TrapRegionManager regionManager;
    private final TrapEffectManager effectManager;
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public TrapManager(TrapRegionManager regionManager, TrapEffectManager effectManager) {
        this.regionManager = regionManager;
        this.effectManager = effectManager;
    }

    public boolean canActivateTrap(Player player, Trap trap) {
        Location loc = player.getLocation();
        World world = loc.getWorld();

        if (VTrap.instance.getConfig().getStringList("exclude-worlds").contains(world.getName())) {
            return false;
        }

        RegionManager rm = container.get(BukkitAdapter.adapt(world));
        if (rm.getApplicableRegions(BukkitAdapter.asBlockVector(loc)).getRegions().stream()
                .anyMatch(r -> VTrap.instance.getConfig().getStringList("exclude-regions").contains(r.getId()))) {
            return false;
        }

        if (regionManager.isLocationInTrapRegion(loc)) return false;

        long now = System.currentTimeMillis();
        long last = playerCooldowns
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .getOrDefault(trap.id(), 0L);

        return player.isOp() || now >= last + (trap.timing().cooldown() * 1000L);
    }

    public boolean activateTrap(Player player, Trap trap) {
        Location loc = player.getLocation();

        ProtectedCuboidRegion region = regionManager.createRegion(loc, trap.id());

        if (region == null) return false;

        if (new SchematicUtil(trap.timing().duration()).spawnSchematic(loc, trap.schematic())) {

            effectManager.applyEffects(player, trap, region);

            playerCooldowns
                    .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                    .put(trap.id(), System.currentTimeMillis());
            return true;
        }
        return false;
    }
}