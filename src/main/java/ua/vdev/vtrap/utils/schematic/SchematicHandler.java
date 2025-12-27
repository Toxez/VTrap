package ua.vdev.vtrap.utils.schematic;

import org.bukkit.Location;

public interface SchematicHandler {
    boolean spawnSchematic(Location location, String fileName);
    int[] getSchematicDimensions(String fileName);
}