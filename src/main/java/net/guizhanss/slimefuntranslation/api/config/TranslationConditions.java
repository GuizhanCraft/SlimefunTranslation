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
public class TranslationConditions {
    /**
     * Whether the translation needs the current item name to match the template item name (color is stripped).
     */
    private boolean matchName;
    private boolean forceLoad;
    private boolean partialOverride;

    @Nonnull
    public static TranslationConditions loadFromConfigurationSection(@Nullable ConfigurationSection section) {
        if (section == null) {
            return new TranslationConditions();
        }
        return TranslationConditions.builder()
            .matchName(section.getBoolean("match-name", false))
            .forceLoad(section.getBoolean("force-load", false))
            .partialOverride(section.getBoolean("partial-override", false))
            .build();
    }

    public void mergeParent(@Nonnull TranslationConditions parent) {
        Preconditions.checkArgument(parent != null, "Parent cannot be null");
        Preconditions.checkArgument(parent != this, "Parent cannot be the same object");

        if (!matchName) {
            matchName = parent.matchName;
        }
        if (!forceLoad) {
            forceLoad = parent.forceLoad;
        }
        if (!partialOverride) {
            partialOverride = parent.partialOverride;
        }
    }
}
