package net.guizhanss.slimefuntranslation.api.config;

/**
 * This record holds the fields to look for in a translation configuration file.
 *
 * @param items      The name of the section to look for item translations.
 * @param lore       The name of the section to look for lore translations.
 * @param messages   The name of the section to look for message translations.
 * @param conditions The name of the section to look for translation conditions.
 */
public record TranslationConfigurationFields(
    String items,
    String lore,
    String messages,
    String conditions
) {
    public static final TranslationConfigurationFields DEFAULT = new TranslationConfigurationFields(
        "translations",
        "lore",
        "messages",
        "conditions"
    );
}
