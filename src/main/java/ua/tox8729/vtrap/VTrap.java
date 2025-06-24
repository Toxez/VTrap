package ua.tox8729.vtrap;

import org.bukkit.plugin.java.JavaPlugin;
import ua.tox8729.vtrap.commands.VTrapCommand;
import ua.tox8729.vtrap.listeners.TrapInteractListener;
import ua.tox8729.vtrap.managers.TrapRegionManager;
import ua.tox8729.vtrap.managers.TrapManager;

import java.io.File;

public final class VTrap extends JavaPlugin {
    public static VTrap instance;
    private TrapRegionManager regionManager;

    @Override
    public void onEnable() {
        instance = this;
        new File(getDataFolder(), "schem").mkdirs();
        saveDefaultConfig();

        regionManager = new TrapRegionManager();
        TrapManager trapManager = new TrapManager(regionManager);
        getServer().getPluginManager().registerEvents(new TrapInteractListener(trapManager), this);
        getCommand("vtrap").setExecutor(new VTrapCommand());
        getCommand("vtrap").setTabCompleter(new VTrapCommand());
    }

    @Override
    public void onDisable() {
        if (regionManager != null) {
            regionManager.clearAllRegions();
        }
    }
}