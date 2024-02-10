package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.List;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.api.interfaces.ItemTranslation;

import lombok.RequiredArgsConstructor;

/**
 * This {@link ItemTranslation} is applied by {@link SlimefunItem}s which implemented {@link TranslatableItem}.
 */
@RequiredArgsConstructor
public class ProgrammedItemTranslation implements ItemTranslation {
    private final String lang;
    private final TranslatableItem translatableItem;

    @Nonnull
    @Override
    public String getDisplayName(@Nonnull String original) {
        return translatableItem.getTranslatedDisplayName(lang, original);
    }

    @Nonnull
    @Override
    public List<String> getLore(@Nonnull List<String> original) {
        return translatableItem.getTranslatedLore(lang, original);
    }
}
