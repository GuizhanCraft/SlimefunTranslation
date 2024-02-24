package net.guizhanss.slimefuntranslation.api.translation;

import net.guizhanss.slimefuntranslation.implementation.translations.FixedItemTranslation;

/**
 * Represents the status of the translation for a specific item.
 *
 * @see FixedItemTranslation
 */
public enum TranslationStatus {
    /**
     * Both item name and lore are allowed to be translated.
     */
    ALLOWED,
    /**
     * Only item name is allowed to be translated.
     */
    NAME_ONLY,
    /**
     * Item is not allowed to be translated.
     */
    DENIED
}
