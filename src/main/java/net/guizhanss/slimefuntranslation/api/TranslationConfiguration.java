package net.guizhanss.slimefuntranslation.api;

import java.util.HashMap;
import java.util.Map;

import net.guizhanss.slimefuntranslation.api.interfaces.Translation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This class holds the information provided from a valid translations file, or from other addons.
 *
 * @author ybw0014
 */
@RequiredArgsConstructor
@Getter
public class TranslationConfiguration {
    private final String name;
    private final String author;
    private final boolean enabled;
    private final String lang;

    private final Map<String, Translation> translations = new HashMap<>();
}
