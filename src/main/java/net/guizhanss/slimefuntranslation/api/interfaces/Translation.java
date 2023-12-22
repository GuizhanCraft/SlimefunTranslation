package net.guizhanss.slimefuntranslation.api.interfaces;

import java.util.List;

import javax.annotation.Nonnull;

import net.guizhanss.slimefuntranslation.api.translations.FixedTranslation;

/**
 * This interface represents a translation.
 *
 * @see FixedTranslation
 */
public interface Translation {
    @Nonnull
    String getDisplayName(String original);

    @Nonnull
    List<String> getLore(List<String> original);
}
