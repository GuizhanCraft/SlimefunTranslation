package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.api.interfaces.ItemTranslation;
import net.guizhanss.slimefuntranslation.utils.ColorUtils;

/**
 * A fixed translation is defined from config file, or from other plugins.
 */
public class FixedItemTranslation implements ItemTranslation {
    private final String displayName;
    private final List<String> lore;
    private final Map<Integer, String> overrides;
    private final Map<Integer, Pair<String, String>> replacements;
    private final boolean checkName;
    private final boolean partialOverride;

    @ParametersAreNonnullByDefault
    public FixedItemTranslation(
        String displayName,
        List<String> lore,
        Map<Integer, String> overrides,
        Map<Integer, Pair<String, String>> replacements,
        boolean checkName,
        boolean partialOverride
    ) {
        this.displayName = ColorUtils.color(displayName);
        this.lore = ColorUtils.color(lore);
        this.overrides = overrides;
        this.replacements = replacements;
        this.checkName = checkName;
        this.partialOverride = partialOverride;
    }

    /**
     * Get the display name of the item.
     * If the defined translated display name is empty, the original display name will be returned.
     *
     * @param original The original display name.
     * @return The translated display name.
     */
    @Override
    @Nonnull
    public String getDisplayName(@Nonnull String original) {
        return displayName.isEmpty() ? original : displayName;
    }

    /**
     * Get the lore of the item.
     * If the defined translated lore is empty, it will start replacing the lore lines.
     *
     * @param original The original lore.
     * @return The translated lore.
     */
    @Override
    @Nonnull
    public List<String> getLore(@Nonnull List<String> original) {
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
        } else if (partialOverride) {
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

    @Override
    @ParametersAreNonnullByDefault
    public boolean canTranslate(ItemStack item, SlimefunItem sfItem) {
        if (!checkName) {
            return true;
        }

        var originalDisplayName = sfItem.getItemName();
        var meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(originalDisplayName);
    }
}
