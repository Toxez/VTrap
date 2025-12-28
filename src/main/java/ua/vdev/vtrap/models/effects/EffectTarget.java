package ua.vdev.vtrap.models.effects;

public enum EffectTarget {
    ACTIVATOR,
    VICTIMS;

    public static EffectTarget fromString(String str) {
        if (str == null) return null;
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}