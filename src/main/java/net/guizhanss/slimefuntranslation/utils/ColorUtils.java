package net.guizhanss.slimefuntranslation.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ColorUtils {
    @Nonnull
    public static String color(@Nonnull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Nonnull
    public static List<String> color(@Nonnull List<String> str) {
        return str.stream().map(ColorUtils::color).collect(Collectors.toCollection(ArrayList::new));
    }

    @Nonnull
    public static String useAltCode(@Nonnull String str) {
        return str.replace('§', '&');
    }

    @Nonnull
    public static List<String> useAltCode(@Nonnull List<String> str) {
        return str.stream().map(ColorUtils::useAltCode).collect(Collectors.toCollection(ArrayList::new));
    }
}
