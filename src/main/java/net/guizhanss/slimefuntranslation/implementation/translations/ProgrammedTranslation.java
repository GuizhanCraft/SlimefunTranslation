package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.List;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.api.interfaces.Translatable;
import net.guizhanss.slimefuntranslation.api.interfaces.Translation;

import lombok.RequiredArgsConstructor;

/**
 * This {@link Translation} is applied by {@link SlimefunItem}s which implemented {@link Translatable}.
 */
@RequiredArgsConstructor
public class ProgrammedTranslation implements Translation {
    private final Translatable translatable;

    @Nonnull
    @Override
    public String getDisplayName(@Nonnull String original) {
        return translatable.getTranslatedDisplayName(original);
    }

    @Nonnull
    @Override
    public List<String> getLore(@Nonnull List<String> original) {
        return translatable.getTranslatedLore(original);
    }
}
