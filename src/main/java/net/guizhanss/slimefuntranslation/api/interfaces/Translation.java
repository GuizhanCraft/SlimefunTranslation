package net.guizhanss.slimefuntranslation.api.interfaces;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.implementation.translations.FixedTranslation;

/**
 * This interface represents a translation.
 *
 * @see FixedTranslation
 */
public interface Translation {
    @Nonnull
    String getDisplayName(@Nonnull String original);

    @Nonnull
    List<String> getLore(@Nonnull List<String> original);

    /**
     * Override this method if you need extra check to make sure item can be translated.
     *
     * @param item
     *     The {@link ItemStack} to check.
     *
     * @return Whether the item can be translated.
     */
    @ParametersAreNonnullByDefault
    default boolean canTranslate(ItemStack item, SlimefunItem sfItem) {
        return true;
    }
}
