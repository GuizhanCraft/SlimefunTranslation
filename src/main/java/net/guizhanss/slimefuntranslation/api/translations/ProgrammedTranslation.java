package net.guizhanss.slimefuntranslation.api.translations;

import lombok.RequiredArgsConstructor;

import net.guizhanss.slimefuntranslation.api.interfaces.Translation;
import net.guizhanss.slimefuntranslation.api.interfaces.Translatable;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import javax.annotation.Nonnull;

import java.util.List;

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
