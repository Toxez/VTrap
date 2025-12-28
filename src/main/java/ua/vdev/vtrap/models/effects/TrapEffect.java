package ua.vdev.vtrap.models.effects;

import org.bukkit.potion.PotionEffect;
import java.util.List;

public record TrapEffect(
        EffectTarget target,
        List<PotionEffect> effects
) {}