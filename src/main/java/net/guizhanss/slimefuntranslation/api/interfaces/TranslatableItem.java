package net.guizhanss.slimefuntranslation.api.interfaces;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This interface should be implemented by a {@link SlimefunItem}.
 * It means the {@link SlimefunItem} can be translated from code.
 */
public interface TranslatableItem {
    @Nonnull
    @ParametersAreNonnullByDefault
    String getTranslatedDisplayName(String language, String original);

    @Nonnull
    @ParametersAreNonnullByDefault
    List<String> getTranslatedLore(String language, List<String> original);
}
