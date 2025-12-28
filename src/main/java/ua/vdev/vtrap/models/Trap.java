package ua.vdev.vtrap.models;

import ua.vdev.vtrap.models.components.TrapTiming;
import ua.vdev.vtrap.models.components.TrapSounds;
import ua.vdev.vtrap.models.components.TrapItemInfo;
import ua.vdev.vtrap.models.effects.TrapEffect;

import java.util.List;

public record Trap(
        String id,
        String schematic,
        TrapTiming timing,
        TrapSounds sounds,
        TrapItemInfo itemInfo,
        List<TrapEffect> effects
) {}