package ua.tox8729.vtrap.utils;

import ua.tox8729.vtrap.VTrap;

public class ConfigUtil {
    public static String getString(String path) {
        return HexUtil.translate(VTrap.instance.getConfig().getString(path));
    }

    public static String getString(String path, String defaultValue) {
        String value = VTrap.instance.getConfig().getString(path);
        return value != null ? HexUtil.translate(value) : defaultValue;
    }

    public static int getInt(String path) {
        return VTrap.instance.getConfig().getInt(path);
    }

    public static boolean getBoolean(String path) {
        return VTrap.instance.getConfig().getBoolean(path);
    }
}