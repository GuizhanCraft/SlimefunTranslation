package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.api.config.TranslationConditions;
import net.guizhanss.slimefuntranslation.api.interfaces.ItemTranslation;
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
        if (lore.isEmpty()) {
            // only line override exists
            var newLore = new ArrayList<>(original);
            for (var entry : overrides.entrySet()) {
                try {
                    newLore.set(entry.getKey() - 1, ColorUtils.color(entry.getValue()));
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
            for (var entry : replacements.entrySet()) {
                try {
                    var line = newLore.get(entry.getKey() - 1);
                    newLore.set(entry.getKey() - 1, ColorUtils.color(line.replace(entry.getValue().getFirstValue(), entry.getValue().getSecondValue())));
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
            return newLore;
        } else if (conditions.isPartialOverride()) {
            List<String> newLore = new ArrayList<>(original);
            for (int i = 0; i < lore.size(); i++) {
                try {
                    var line = lore.get(i);
                    newLore.set(i, ColorUtils.color(line));
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
            return newLore;
        } else {
            return new ArrayList<>(lore);
        }
    }

    /**
     * Check if the item can be translated.
     *
     * @param user The {@link User} to get the display name for.
     * @param item The {@link ItemStack}.
     * @param meta The {@link ItemMeta} of the item.
     * @param sfId The {@link SlimefunItem} id of the item.
     * @return
     */
    @Override
    @ParametersAreNonnullByDefault
    public boolean canTranslate(User user, ItemStack item, ItemMeta meta, String sfId) {
        if (!conditions.isMatchName() || conditions.isForceLoad()) {
            return true;
        }

        SlimefunItem sfItem = SlimefunItem.getById(sfId);
        if (sfItem == null) {
            return false;
        }

        var originalDisplayName = sfItem.getItemName();
        return meta.hasDisplayName() && meta.getDisplayName().equals(originalDisplayName);
    }
}
