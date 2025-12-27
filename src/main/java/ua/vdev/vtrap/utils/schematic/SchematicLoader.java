package ua.vdev.vtrap.utils.schematic;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import java.io.File;
import java.nio.file.Files;

public class SchematicLoader {
    private final File schematicsFolder;

    public SchematicLoader(File dataFolder) {
        this.schematicsFolder = new File(dataFolder, "schem");
    }

    public Clipboard loadSchematic(String fileName) {
        File file = new File(schematicsFolder, fileName);
        if (!file.exists()) {
            return null;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            return null;
        }

        try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
            return reader.read();
        } catch (Exception e) {
            return null;
        }
    }

    public File getSchematicFile(String fileName) {
        File file = new File(schematicsFolder, fileName);
        return file.exists() ? file : null;
    }
}