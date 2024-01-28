package net.guizhanss.slimefuntranslation.utils.constant;

import lombok.experimental.UtilityClass;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;

@UtilityClass
public final class Keys {
    public static final NamespacedKey SEARCH_DISPLAY = create("search_display");

    @Nonnull
    public static NamespacedKey create(@Nonnull String key) {
        return new NamespacedKey(SlimefunTranslation.getInstance(), key);
    }
}
