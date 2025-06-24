package ua.tox8729.vtrap.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class HexUtil {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?i)&#([a-f0-9]{6})");

    public static @NotNull String translate(final String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String result = HEX_COLOR_PATTERN.matcher(value)
                .replaceAll(match -> ChatColor.of("#" + match.group(1)).toString());

        result = ChatColor.translateAlternateColorCodes('&', result);
        return result.replace("\\n", "\n");
    }
}
