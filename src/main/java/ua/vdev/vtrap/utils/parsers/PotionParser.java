package ua.vdev.vtrap.utils.parsers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PotionParser {

    private PotionParser() {}

    public static List<PotionEffect> parse(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(PotionParser::parseSingleEffect)
                .filter(Objects::nonNull)
                .toList();
    }

    private static PotionEffect parseSingleEffect(String part) {
        String[] args = part.split(":");
        if (args.length != 3) return null;

        PotionEffectType type = PotionEffectType.getByName(args[0].toUpperCase());
        if (type == null) return null;

        int amplifier = Integer.parseInt(args[1]);
        int durationTicks = Integer.parseInt(args[2]) * 20;

        return new PotionEffect(type, durationTicks, amplifier);
    }
}