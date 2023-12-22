package net.guizhanss.slimefuntranslation.api.translations;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.guizhanss.slimefuntranslation.api.interfaces.Translation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedTranslation implements Translation {
    private final String displayName;
    private final List<String> lore;
    private final Map<Integer, String> replacements;

    @Override
    @Nonnull
    public String getDisplayName(@Nonnull String original) {
        return displayName;
    }
}
