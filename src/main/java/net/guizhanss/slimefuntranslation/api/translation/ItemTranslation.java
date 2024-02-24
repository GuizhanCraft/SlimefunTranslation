package net.guizhanss.slimefuntranslation.api.translation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.implementation.translations.FixedItemTranslation;
import net.guizhanss.slimefuntranslation.implementation.translations.ProgrammedItemTranslation;

/**
 * This interface represents an item translation.
 *
 * @see FixedItemTranslation
 * @see ProgrammedItemTranslation
 */
public interface ItemTranslation {
    /**
     * Get the translated display name of the item.
     *
     * @param user     The {@link User} to get the display name for.
     * @param item     The {@link ItemStack}.
     * @param meta     The {@link ItemMeta} of item.
     * @param original The original display name of the item.
     * @return The translated display name of the item.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    String getDisplayName(User user, ItemStack item, ItemMeta meta, String original);

    /**
     * Get the translated lore of the item.
     *
     * @param user     The {@link User} to get the lore for.
     * @param item     The {@link ItemStack}.
     * @param meta     The {@link ItemMeta} of item.
     * @param original The original lore of the item.
     * @return The translated lore of the item.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    List<String> getLore(User user, ItemStack item, ItemMeta meta, List<String> original);

    /**
     * Override this method if you need extra check to make sure item can be translated.
     *
     * @param user The {@link User} to check.
     * @param item The {@link ItemStack} to check.
     * @param meta The {@link ItemMeta} to check.
     * @param sfId The {@link SlimefunItem} id of the item.
     * @return The {@link TranslationStatus} for the item.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    default TranslationStatus canTranslate(User user, ItemStack item, ItemMeta meta, String sfId) {
        return TranslationStatus.ALLOWED;
    }
}
