package ua.vdev.vtrap.models.factory;

import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.models.components.TrapTiming;
import ua.vdev.vtrap.models.components.TrapSounds;
import ua.vdev.vtrap.models.components.TrapItemInfo;
import ua.vdev.vtrap.utils.ConfigUtil;
import ua.vdev.vtrap.utils.HexUtil;

import java.util.stream.Collectors;

public final class TrapFactory {

    private TrapFactory() {}

    public static Trap create(String trapId) {
        if (!VTrap.instance.getConfig().contains("traps." + trapId)) {
            return null;
        }

        String base = "traps." + trapId + ".";

        TrapTiming timing = new TrapTiming(
                VTrap.instance.getConfig().getInt(base + "duration"),
                VTrap.instance.getConfig().getInt(base + "cooldown")
        );

        TrapSounds sounds = new TrapSounds(
                ConfigUtil.getString(base + "sound"),
                ConfigUtil.getString(base + "despawn_sound")
        );

        TrapItemInfo itemInfo = new TrapItemInfo(
                ConfigUtil.getString(base + "material"),
                ConfigUtil.getString(base + "name"),
                VTrap.instance.getConfig().getStringList(base + "lore").stream()
                        .map(HexUtil::translate)
                        .collect(Collectors.toUnmodifiableList())
        );

        return new Trap(
                trapId,
                VTrap.instance.getConfig().getString(base + "schematic"),
                timing,
                sounds,
                itemInfo
        );
    }
}