package ua.tox8729.vtrap.utils.schematic;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ua.tox8729.vtrap.VTrap;

public class SchematicOperation {
    private final VTrap plugin;

    public SchematicOperation(VTrap plugin) {
        this.plugin = plugin;
    }

    public boolean spawnSchematic(Location location, String fileName, SchematicLoader loader, int timeUndo) {
        Clipboard clipboard = loader.loadSchematic(fileName);
        if (clipboard == null) {
            return false;
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(position)
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            scheduleUndo(editSession, timeUndo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void scheduleUndo(EditSession editSession, int timeUndo) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (EditSession session = editSession) {
                    session.undo(session);
                }
            }
        }.runTaskLater(plugin, timeUndo);
    }
}