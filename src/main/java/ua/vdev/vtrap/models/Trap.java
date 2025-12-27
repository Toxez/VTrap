package ua.vdev.vtrap.models;

import ua.vdev.vtrap.models.components.TrapTiming;
import ua.vdev.vtrap.models.components.TrapSounds;
import ua.vdev.vtrap.models.components.TrapItemInfo;

public record Trap(
        String id,
        String schematic,
        TrapTiming timing,
        TrapSounds sounds,
        TrapItemInfo itemInfo
) {}