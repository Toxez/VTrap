package ua.vdev.vtrap.utils.schematic;

import org.bukkit.Location;
import ua.vdev.vtrap.VTrap;

public class SchematicUtil implements SchematicHandler {
    private final SchematicLoader loader;
    private final SchematicOperation operation;
    private final SchematicDimensionReader dimensionReader;
    private final int undoTicks;

    public SchematicUtil(int duration) {
        this.undoTicks = duration * 20;
        this.loader = new SchematicLoader(VTrap.instance.getDataFolder());
        this.operation = new SchematicOperation(VTrap.instance);
        this.dimensionReader = new SchematicDimensionReader(loader);
    }

    @Override
    public boolean spawnSchematic(Location location, String fileName) {
        return operation.spawnSchematic(location, fileName, loader, undoTicks);
    }

    @Override
    public int[] getSchematicDimensions(String fileName) {
        return dimensionReader.getSchematicDimensions(fileName);
    }
}