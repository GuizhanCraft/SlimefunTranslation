package net.guizhanss.slimefuntranslation.api.interfaces;

import java.util.List;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This interface should be implemented by a {@link SlimefunItem}.
 * It means the {@link SlimefunItem} can be translated from code.
 */
public interface Translatable {
    @Nonnull
    String getTranslatedDisplayName(String original);

    @Nonnull
    List<String> getTranslatedLore(List<String> original);
}
