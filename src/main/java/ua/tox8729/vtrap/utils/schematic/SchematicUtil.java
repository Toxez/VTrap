package ua.tox8729.vtrap.utils.schematic;

import org.bukkit.Location;
import ua.tox8729.vtrap.VTrap;

public class SchematicUtil {
    private final SchematicLoader schematicLoader;
    private final SchematicOperation schematicOperation;
    private final SchematicDimensionReader dimensionReader;
    private final int timeUndo;

    public SchematicUtil(int duration) {
        this.timeUndo = duration * 20;
        this.schematicLoader = new SchematicLoader(VTrap.instance.getDataFolder());
        this.schematicOperation = new SchematicOperation(VTrap.instance);
        this.dimensionReader = new SchematicDimensionReader(schematicLoader);
    }

    public boolean spawnSchematic(Location location, String fileName) {
        return schematicOperation.spawnSchematic(location, fileName, schematicLoader, timeUndo);
    }

    public int[] getSchematicDimensions(String fileName) {
        return dimensionReader.getSchematicDimensions(fileName);
    }
}