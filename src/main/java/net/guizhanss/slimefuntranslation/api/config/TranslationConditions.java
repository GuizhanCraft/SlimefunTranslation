package net.guizhanss.slimefuntranslation.api.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import org.bukkit.configuration.ConfigurationSection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class holds all the conditions for a translation.
 * <p>
 * Can be applied to a translation configuration or a single item translation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class TranslationConditions {
    private static final String KEY_MATCH_NAME = "match-name";
    private static final String KEY_MATCH_LORE = "match-lore";
    private static final String KEY_FORCE_LOAD = "force-load";
    private static final String KEY_PARTIAL_OVERRIDE = "partial-override";

    /**
     * Whether the translation needs the current item name to match the template item name (color is stripped).
     */
    private boolean matchName;
    /**
     * Whether the translation needs the current item lore to match the template item lore.
     */
    private boolean matchLore;
    /**
     * Whether to ignore item ID check.
     */
    private boolean forceLoad;
    /**
     * Whether to enable partial override.
     */
    private boolean partialOverride;

    /**
     * Load the translation conditions from a {@link ConfigurationSection}.
     *
     * @param section The {@link ConfigurationSection} to load from.
     * @return The loaded {@link TranslationConditions}.
     */
    @Nonnull
    public static TranslationConditions load(@Nullable ConfigurationSection section) {
        // the default conditions are all false
        return load(TranslationConditions.builder().build(), section);
    }

    /**
     * Load the translation conditions from a {@link ConfigurationSection},
     * with the default value provided by parent {@link TranslationConditions}.
     *
     * @param parent  The parent {@link TranslationConditions}.
     * @param section The {@link ConfigurationSection} to load from.
     * @return The loaded {@link TranslationConditions}.
     */
    public static TranslationConditions load(@Nonnull TranslationConditions parent, @Nullable ConfigurationSection section) {
        Preconditions.checkArgument(parent != null, "parent cannot be null");
        if (section == null) {
            return parent;
        }
        return TranslationConditions.builder()
            .matchName(section.getBoolean(KEY_MATCH_NAME, parent.matchName))
            .matchLore(section.getBoolean(KEY_MATCH_LORE, parent.matchLore))
            .forceLoad(section.getBoolean(KEY_FORCE_LOAD, parent.forceLoad))
            .partialOverride(section.getBoolean(KEY_PARTIAL_OVERRIDE, parent.partialOverride))
            .build();
    }
}
