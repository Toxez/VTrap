package ua.vdev.vtrap.models.factory;

import ua.vdev.vtrap.VTrap;
import ua.vdev.vtrap.models.Trap;
import ua.vdev.vtrap.models.components.TrapTiming;
import ua.vdev.vtrap.models.components.TrapSounds;
import ua.vdev.vtrap.models.components.TrapItemInfo;
import ua.vdev.vtrap.models.effects.EffectTarget;
import ua.vdev.vtrap.models.effects.TrapEffect;
import ua.vdev.vtrap.utils.ConfigUtil;
import ua.vdev.vtrap.utils.HexUtil;
import ua.vdev.vtrap.utils.parsers.PotionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        List<TrapEffect> trapEffects = new ArrayList<>();
        List<Map<?, ?>> effectsList = VTrap.instance.getConfig().getMapList(base + "effects");

        for (Map<?, ?> entry : effectsList) {
            String typeStr = (String) entry.get("type");
            String effectStr = (String) entry.get("effect");

            EffectTarget target = EffectTarget.fromString(typeStr);
            if (target != null && effectStr != null) {
                trapEffects.add(new TrapEffect(target, PotionParser.parse(effectStr)));
            }
        }

        return new Trap(
                trapId,
                VTrap.instance.getConfig().getString(base + "schematic"),
                timing,
                sounds,
                itemInfo,
                trapEffects
        );
    }
}