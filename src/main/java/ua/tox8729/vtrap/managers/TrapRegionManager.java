package ua.tox8729.vtrap.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;
import ua.tox8729.vtrap.VTrap;
import ua.tox8729.vtrap.utils.ConfigUtil;
import ua.tox8729.vtrap.utils.schematic.SchematicUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrapRegionManager {
    private final Map<UUID, ProtectedCuboidRegion> activeTrapRegions = new HashMap<>();
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public boolean createRegion(Location location, String trapId) {
        String schematicName = VTrap.instance.getConfig().getString("traps." + trapId + ".schematic");
        int[] dims = new SchematicUtil(0).getSchematicDimensions(schematicName);
        if (dims == null) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        int width = dims[0];
        int height = dims[1];
        int length = dims[2];

        int minX = x - (width / 2);
        int minZ = z - (length / 2);
        int minY = y - 1;
        int maxX = minX + width - 1;
        int maxZ = minZ + length - 1;
        int maxY = minY + height;

        ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion(
                UUID.randomUUID().toString(),
                BlockVector3.at(minX, minY, minZ),
                BlockVector3.at(maxX, maxY, maxZ)
        );

        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (activeTrapRegions.values().stream().anyMatch(r -> !newRegion.getIntersectingRegions(List.of(r)).isEmpty())) {
            return false;
        }

        newRegion.setFlag(Flags.USE, StateFlag.State.DENY);
        newRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        newRegion.setFlag(Flags.BUILD, StateFlag.State.DENY);
        newRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        newRegion.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
        newRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);

        regionManager.addRegion(newRegion);
        UUID regionId = UUID.fromString(newRegion.getId());
        activeTrapRegions.put(regionId, newRegion);

        new BukkitRunnable() {
            @Override
            public void run() {
                deleteRegion(location.getWorld(), regionId, trapId);
            }
        }.runTaskLater(VTrap.instance, VTrap.instance.getConfig().getInt("traps." + trapId + ".duration") * 20L);

        return true;
    }

    public void deleteRegion(World world, UUID regionId, String trapId) {
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        ProtectedCuboidRegion region = activeTrapRegions.remove(regionId);
        if (region != null) {
            regionManager.removeRegion(region.getId());
            BlockVector3 minPoint = region.getMinimumPoint();
            Location soundLocation = new Location(world, minPoint.getX(), minPoint.getY(), minPoint.getZ());
            world.playSound(soundLocation, Sound.valueOf(ConfigUtil.getString("traps." + trapId + ".despawn_sound")),
                    SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    public boolean isLocationInTrapRegion(Location location) {
        return activeTrapRegions.values().stream().anyMatch(r -> r.contains(BukkitAdapter.asBlockVector(location)));
    }

    public void clearAllRegions() {
        for (Map.Entry<UUID, ProtectedCuboidRegion> entry : new HashMap<>(activeTrapRegions).entrySet()) {
            UUID regionId = entry.getKey();
            ProtectedCuboidRegion region = entry.getValue();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(getWorldFromRegion(region)));
            if (regionManager != null) {
                regionManager.removeRegion(region.getId());
            }
            activeTrapRegions.remove(regionId);
        }
    }

    private World getWorldFromRegion(ProtectedCuboidRegion region) {
        BlockVector3 minPoint = region.getMinimumPoint();
        for (World world : VTrap.instance.getServer().getWorlds()) {
            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm != null && rm.getRegions().containsKey(region.getId())) {
                return world;
            }
        }
        return null;
    }
}