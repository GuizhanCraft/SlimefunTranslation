package net.guizhanss.slimefuntranslation.implementation.translations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.api.interfaces.ItemTranslation;
import net.guizhanss.slimefuntranslation.utils.ColorUtils;

/**
 * A fixed translation is defined from config file, or from other plugins.
 */
public class FixedItemTranslation implements ItemTranslation {
    private final String displayName;
    private final List<String> lore;
    private final Map<Integer, String> replacements;
    private final boolean checkName;

    public FixedItemTranslation(String displayName, List<String> lore, Map<Integer, String> replacements, boolean checkName) {
        this.displayName = ColorUtils.color(displayName);
        this.lore = ColorUtils.color(lore);
        this.replacements = replacements;
        this.checkName = checkName;
    }

    /**
     * Get the display name of the item.
     * If the defined translated display name is empty, the original display name will be returned.
     *
     * @param original
     *     The original display name.
     *
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
     * @param original
     *     The original lore.
     *
     * @return The translated lore.
     */
    @Override
    @Nonnull
    public List<String> getLore(@Nonnull List<String> original) {
        if (lore.isEmpty()) {
            var newLore = new ArrayList<>(original);
            for (var entry : replacements.entrySet()) {
                try {
                    newLore.set(entry.getKey() - 1, ColorUtils.color(entry.getValue()));
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