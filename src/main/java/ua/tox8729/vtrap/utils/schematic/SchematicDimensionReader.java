package ua.tox8729.vtrap.utils.schematic;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class SchematicDimensionReader {
    private final SchematicLoader schematicLoader;

    public SchematicDimensionReader(SchematicLoader schematicLoader) {
        this.schematicLoader = schematicLoader;
    }

    public int[] getSchematicDimensions(String fileName) {
        Clipboard clipboard = schematicLoader.loadSchematic(fileName);
        if (clipboard == null) {
            return null;
        }

        return new int[]{
                clipboard.getRegion().getWidth(),
                clipboard.getRegion().getHeight(),
                clipboard.getRegion().getLength()
        };
    }
}