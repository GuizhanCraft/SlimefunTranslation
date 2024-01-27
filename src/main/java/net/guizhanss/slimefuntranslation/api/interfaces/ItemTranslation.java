package net.guizhanss.slimefuntranslation.api.interfaces;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.implementation.translations.FixedItemTranslation;

/**
 * This interface represents an item translation.
 *
 * @see FixedItemTranslation
 */
public interface ItemTranslation {
    @Nonnull
    String getDisplayName(@Nonnull String original);

    @Nonnull
    List<String> getLore(@Nonnull List<String> original);

    /**
     * Override this method if you need extra check to make sure item can be translated.
     *
     * @param item The {@link ItemStack} to check.
     * @param sfId The {@link SlimefunItem} id of the item.
     * @return Whether the item can be translated.
     */
    @ParametersAreNonnullByDefault
    default boolean canTranslate(ItemStack item, String sfId) {
        return true;
    }
}
