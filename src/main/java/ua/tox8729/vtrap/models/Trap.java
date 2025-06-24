package ua.tox8729.vtrap.models;

import ua.tox8729.vtrap.VTrap;
import ua.tox8729.vtrap.utils.ConfigUtil;
import ua.tox8729.vtrap.utils.HexUtil;

import java.util.List;

public class Trap {
    private final String id;
    private final String schematic;
    private final int duration;
    private final int cooldown;
    private final String sound;
    private final String despawnSound;
    private final String material;
    private final String displayName;
    private final List<String> lore;

    private Trap(String id, String schematic, int duration, int cooldown, String sound, String despawnSound, String material, String displayName, List<String> lore) {
        this.id = id;
        this.schematic = schematic;
        this.duration = duration;
        this.cooldown = cooldown;
        this.sound = sound;
        this.despawnSound = despawnSound;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public static Trap fromConfig(String trapId) {
        if (!VTrap.instance.getConfig().contains("traps." + trapId)) {
            return null;
        }
        return new Trap(
                trapId,
                VTrap.instance.getConfig().getString("traps." + trapId + ".schematic"),
                VTrap.instance.getConfig().getInt("traps." + trapId + ".duration"),
                VTrap.instance.getConfig().getInt("traps." + trapId + ".cooldown"),
                ConfigUtil.getString("traps." + trapId + ".sound"),
                ConfigUtil.getString("traps." + trapId + ".despawn_sound"),
                ConfigUtil.getString("traps." + trapId + ".material"),
                ConfigUtil.getString("traps." + trapId + ".name"),
                VTrap.instance.getConfig().getStringList("traps." + trapId + ".lore").stream()
                        .map(HexUtil::translate)
                        .toList()
        );
    }

    public String getId() {
        return id;
    }

    public String getSchematic() {
        return schematic;
    }

    public int getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public String getSound() {
        return sound;
    }

    public String getDespawnSound() {
        return despawnSound;
    }

    public String getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}