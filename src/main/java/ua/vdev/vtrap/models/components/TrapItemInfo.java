package ua.vdev.vtrap.models.components;

import java.util.List;

public record TrapItemInfo(
        String material,
        String displayName,
        List<String> lore
) {}