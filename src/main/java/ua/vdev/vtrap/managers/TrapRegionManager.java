package ua.vdev.vtrap.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.utils.ConfigUtil;
import ua.vdev.vtrap.utils.FlagConfigHelper;
import ua.vdev.vtrap.utils.schematic.SchematicUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrapRegionManager {
    private final Map<UUID, ProtectedCuboidRegion> activeTrapRegions = new HashMap<>();
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public ProtectedCuboidRegion createRegion(Location location, String trapId) {
        String schematicName = VTrap.instance.getConfig().getString("traps." + trapId + ".schematic");
        int[] dims = new SchematicUtil(0).getSchematicDimensions(schematicName);
        if (dims == null) return null;

        int width = dims[0], height = dims[1], length = dims[2];
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                UUID.randomUUID().toString(),
                BlockVector3.at(x - width / 2, y - 1, z - length / 2),
                BlockVector3.at(x - width / 2 + width - 1, y - 1 + height, z - length / 2 + length - 1)
        );

        if (activeTrapRegions.values().stream()
                .anyMatch(r -> !region.getIntersectingRegions(List.of(r)).isEmpty())) {
            return null;
        }

        Map<StateFlag, StateFlag.State> flags = FlagConfigHelper.getFlagsForTrap(trapId);
        flags.forEach(region::setFlag);

        RegionManager rm = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (rm == null) return null;
        rm.addRegion(region);

        UUID regionId = UUID.fromString(region.getId());
        activeTrapRegions.put(regionId, region);

        int duration = VTrap.instance.getConfig().getInt("traps." + trapId + ".duration");
        new BukkitRunnable() {
            @Override
            public void run() {
                deleteRegion(location.getWorld(), regionId, trapId);
            }
        }.runTaskLater(VTrap.instance, duration * 20L);

        return region;
    }

    public void deleteRegion(World world, UUID regionId, String trapId) {
        ProtectedCuboidRegion region = activeTrapRegions.remove(regionId);
        if (region == null) return;

        RegionManager rm = container.get(BukkitAdapter.adapt(world));
        if (rm != null) {
            rm.removeRegion(region.getId());
        }

        Location soundLoc = new Location(world,
                region.getMinimumPoint().getX(),
                region.getMinimumPoint().getY(),
                region.getMinimumPoint().getZ());

        world.playSound(soundLoc,
                Sound.valueOf(ConfigUtil.getString("traps." + trapId + ".despawn_sound")),
                SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public boolean isLocationInTrapRegion(Location location) {
        return activeTrapRegions.values().stream()
                .anyMatch(r -> r.contains(BukkitAdapter.asBlockVector(location)));
    }

    public void clearAllRegions() {
        new HashMap<>(activeTrapRegions).forEach((id, region) -> {
            World world = getWorldFromRegion(region);
            if (world != null) {
                RegionManager rm = container.get(BukkitAdapter.adapt(world));
                if (rm != null) rm.removeRegion(region.getId());
            }
        });
        activeTrapRegions.clear();
    }

    private World getWorldFromRegion(ProtectedCuboidRegion region) {
        return VTrap.instance.getServer().getWorlds().stream()
                .filter(w -> {
                    RegionManager rm = container.get(BukkitAdapter.adapt(w));
                    return rm != null && rm.getRegions().containsKey(region.getId());
                })
                .findFirst()
                .orElse(null);
    }
}