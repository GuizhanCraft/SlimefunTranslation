package net.guizhanss.slimefuntranslation.api.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This class holds the fields to look for in a translation configuration file.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class TranslationConfigurationFields {
    public static final TranslationConfigurationFields DEFAULT = TranslationConfigurationFields.builder().build();

    @Builder.Default
    private String items = "translations";
    @Builder.Default
    private String lore = "lore";
    @Builder.Default
    private String messages = "messages";
    @Builder.Default
    private String conditions = "conditions";
}
