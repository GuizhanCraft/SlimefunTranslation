package net.guizhanss.slimefuntranslation.api.config;

import java.util.HashMap;
import java.util.Map;

import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;

import lombok.Getter;

/**
 * This class holds all the translations within a {@link TranslationConfiguration}.
 */
@Getter
public class Translations {
    private final Map<String, ItemTranslation> guide = new HashMap<>();
    private final Map<String, String> itemGroup = new HashMap<>();
    private final Map<String, ItemTranslation> item = new HashMap<>();
    private final Map<String, String> lore = new HashMap<>();
    private final Map<String, String> message = new HashMap<>();
}
