package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.api.config.TranslationConditions;
import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;
import net.guizhanss.slimefuntranslation.api.translation.TranslationStatus;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.ColorUtils;

/**
 * A fixed translation is defined from config file, or from other plugins.
 */
public class FixedItemTranslation implements ItemTranslation {
    private final String displayName;
    private final List<String> lore;
    private final Map<Integer, String> overrides;
    private final Map<Integer, Pair<String, String>> replacements;
    private final TranslationConditions conditions;

    @ParametersAreNonnullByDefault
    public FixedItemTranslation(
        String displayName,
        List<String> lore,
        Map<Integer, String> overrides,
        Map<Integer, Pair<String, String>> replacements,
        TranslationConditions conditions
    ) {
        this.displayName = ColorUtils.color(displayName);
        this.lore = ColorUtils.color(lore);
        this.overrides = overrides;
        this.replacements = replacements;
        this.conditions = conditions;
    }

    /**
     * Get the display name of the item.
     * If the defined translated display name is empty, the original display name will be returned.
     *
     * @param user     The {@link User} to get the display name for.
     * @param item     The {@link ItemStack}.
     * @param meta     The {@link ItemMeta} of the item.
     * @param original The original display name.
     * @return The translated display name.
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getDisplayName(User user, ItemStack item, ItemMeta meta, String original) {
        return displayName.isEmpty() ? original : displayName;
    }

    /**
     * Get the lore of the item.
     * If the defined translated lore is empty, it will start replacing the lore lines.
     *
     * @param user     The {@link User} to get the display name for.
     * @param item     The {@link ItemStack}.
     * @param meta     The {@link ItemMeta} of the item.
     * @param original The original lore.
     * @return The translated lore.
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getLore(User user, ItemStack item, ItemMeta meta, List<String> original) {
        List<String> newLore = new ArrayList<>(original);

        if (!lore.isEmpty()) {
            // lore specified
            if (conditions.isPartialOverride()) {
                // partial override enabled, only override the specified lines
                for (int i = 0; i < lore.size(); i++) {
                    try {
                        var line = lore.get(i);
                        newLore.set(i, ColorUtils.color(line));
                    } catch (IndexOutOfBoundsException e) {
                        // ignore
                    }
                }
            } else {
                // partial override disabled, override all lines
                newLore = new ArrayList<>(lore);
            }
        }

        // specific line overrides
        for (var entry : overrides.entrySet()) {
            try {
                newLore.set(entry.getKey() - 1, ColorUtils.color(entry.getValue()));
            } catch (IndexOutOfBoundsException e) {
                // ignore
            }
        }
        // specific line replacements
        for (var entry : replacements.entrySet()) {
            try {
                var line = newLore.get(entry.getKey() - 1);
                newLore.set(entry.getKey() - 1, ColorUtils.color(line.replace(entry.getValue().getFirstValue(), entry.getValue().getSecondValue())));
            } catch (IndexOutOfBoundsException e) {
                // ignore
            }
        }

        return newLore;
    }

    /**
     * Check if the item can be translated.
     *
     * @param user The {@link User} to get the display name for.
     * @param item The {@link ItemStack}.
     * @param meta The {@link ItemMeta} of the item.
     * @param sfId The {@link SlimefunItem} id of the item.
     * @return The {@link TranslationStatus} for the item.
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TranslationStatus canTranslate(User user, ItemStack item, ItemMeta meta, String sfId) {
        if (conditions.isForceLoad()) {
            return TranslationStatus.ALLOWED;
        }

        SlimefunItem sfItem = SlimefunItem.getById(sfId);
        if (sfItem == null) {
            return TranslationStatus.DENIED;
        }

        if (conditions.isMatchName()) {
            var originalDisplayName = sfItem.getItemName();
            if (!meta.hasDisplayName() || !meta.getDisplayName().equals(originalDisplayName)) {
                return TranslationStatus.DENIED;
            }
        }

        if (conditions.isMatchLore()) {
            var originalItem = sfItem.getItem();
            if (originalItem instanceof SlimefunItemStack sfItemStack) {
                var originalLore = sfItemStack.getItemMetaSnapshot().getLore();
                if (originalLore.isEmpty() || !meta.hasLore() || !meta.getLore().equals(originalLore.get())) {
                    return TranslationStatus.NAME_ONLY;
                }
            } else {
                var originalMeta = originalItem.getItemMeta();
                if (!originalMeta.hasLore()) {
                    return TranslationStatus.DENIED;
                }
                var originalLore = originalMeta.getLore();
                if (!meta.hasLore() || !meta.getLore().equals(originalLore)) {
                    return TranslationStatus.NAME_ONLY;
                }
            }
        }

        return TranslationStatus.ALLOWED;
    }
}
