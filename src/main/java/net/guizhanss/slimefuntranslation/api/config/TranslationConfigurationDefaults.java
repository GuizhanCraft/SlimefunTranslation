package net.guizhanss.slimefuntranslation.api.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class TranslationConfigurationDefaults {
    public static final TranslationConfigurationDefaults DEFAULT = TranslationConfigurationDefaults.builder().build();

    @Builder.Default
    private String name = "Unnamed Translation";
    @Builder.Default
    private String prefix = "";
    @Builder.Default
    private String suffix = "";
}
