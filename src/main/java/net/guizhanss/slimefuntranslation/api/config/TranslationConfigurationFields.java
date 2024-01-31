package net.guizhanss.slimefuntranslation.api.config;

public record TranslationConfigurationFields(
    String items,
    String lore,
    String messages
) {
    public static final TranslationConfigurationFields DEFAULT = new TranslationConfigurationFields(
        "translations",
        "lore",
        "messages"
    );
}
