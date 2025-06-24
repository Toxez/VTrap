package ua.tox8729.vtrap.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ua.tox8729.vtrap.VTrap;
import ua.tox8729.vtrap.models.Trap;
import ua.tox8729.vtrap.utils.schematic.SchematicUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrapManager {
    private final Map<UUID, Map<String, Long>> playerCooldowns = new HashMap<>();
    private final TrapRegionManager regionManager;
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public TrapManager(TrapRegionManager regionManager) {
        this.regionManager = regionManager;
    }

    public boolean canActivateTrap(Player player, Trap trap) {
        Location location = player.getLocation();
        World world = location.getWorld();

        if (VTrap.instance.getConfig().getStringList("exclude-worlds").contains(world.getName())) {
            return false;
        }

        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location))
                .getRegions().stream()
                .anyMatch(r -> VTrap.instance.getConfig().getStringList("exclude-regions").contains(r.getId()))) {
            return false;
        }

        if (this.regionManager.isLocationInTrapRegion(location)) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        Map<String, Long> playerTrapCooldowns = playerCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        long lastUse = playerTrapCooldowns.getOrDefault(trap.getId(), 0L);
        if (!player.isOp() && lastUse + (trap.getCooldown() * 1000L) > currentTime) {
            return false;
        }

        return true;
    }

    public boolean activateTrap(Player player, Trap trap) {
        Location location = player.getLocation();
        if (regionManager.createRegion(location, trap.getId())) {
            if (new SchematicUtil(trap.getDuration()).spawnSchematic(location, trap.getSchematic())) {
                playerCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                        .put(trap.getId(), System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }
}