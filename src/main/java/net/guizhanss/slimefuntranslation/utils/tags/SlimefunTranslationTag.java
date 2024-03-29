package net.guizhanss.slimefuntranslation.utils.tags;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.exceptions.TagMisconfigurationException;

/**
 * Tags for some materials.
 *
 * @author ybw0014
 * @author haiman233
 */
@SuppressWarnings("ConstantConditions")
public enum SlimefunTranslationTag implements Tag<Material> {
    /**
     * This includes all swords.
     */
    SWORD,

    /**
     * This includes all helmets.
     */
    HELMET,

    /**
     * This includes all chestplates.
     */
    CHESTPLATE,

    /**
     * This includes all leggings.
     */
    LEGGINGS,

    /**
     * This includes all boots.
     */
    BOOTS,

    /**
     * This includes all horse armors.
     */
    HORSE_ARMOR,

    /**
     * This includes all axes.
     */
    AXE,

    /**
     * This includes all pickaxes.
     */
    PICKAXE,

    /**
     * This includes all shovels.
     */
    SHOVEL,

    /**
     * This includes all hoes.
     */
    HOE,

    /**
     * This includes all fishing rods.
     */
    FISHING_ROD,

    /**
     * This includes all bows.
     */
    BOW,

    /**
     * This includes all kinds of heads.
     */
    HEAD,

    /**
     * This includes all carpets.
     */
    CARPET,

    /**
     * This includes all materials that is suitable for main hand slot.
     */
    HAND_SLOT,

    /**
     * This includes all materials that is suitable for off hand slot.
     */
    OFF_HAND_SLOT,

    /**
     * This includes all materials that is suitable for head slot.
     */
    HEAD_SLOT,

    /**
     * This includes all materials that is suitable for chest slot.
     */
    CHEST_SLOT,

    /**
     * This includes all materials that is suitable for legs slot.
     */
    LEGS_SLOT,

    /**
     * This includes all materials that is suitable for feet slot.
     */
    FEET_SLOT;

    /**
     * Cached values.
     */
    private static final SlimefunTranslationTag[] cachedValues = values();

    /**
     * Lookup table for tag names.
     */
    private static final Map<String, SlimefunTranslationTag> nameLookup = new HashMap<>();

    static {
        for (SlimefunTranslationTag tag : cachedValues) {
            nameLookup.put(tag.name(), tag);
        }
    }

    private final NamespacedKey key;
    private final Set<Material> materials = new HashSet<>();
    private final Set<Tag<Material>> additionalTags = new HashSet<>();

    SlimefunTranslationTag() {
        this.key = new NamespacedKey(SlimefunTranslation.getInstance(), name().toLowerCase(Locale.ROOT));
    }

    /**
     * Reload all tags.
     */
    public static void reloadAll() {
        for (SlimefunTranslationTag tag : cachedValues) {
            tag.reload();
        }
    }

    /**
     * Get the {@link SlimefunTranslationTag} with specified name.
     *
     * @param value The name of {@link SlimefunTranslationTag}.
     * @return The {@link SlimefunTranslationTag}. Null if name doesn't exist.
     */
    @Nullable
    public static SlimefunTranslationTag getTag(@Nonnull String value) {
        Preconditions.checkArgument(value != null, "Tag cannot be null!");

        return nameLookup.get(value);
    }

    /**
     * Reload this {@link SlimefunTranslationTag} from resources.
     */
    public void reload() {
        try {
            new TagParser(this).parse((materialSet, additionalTagSet) -> {
                materials.clear();
                materials.addAll(materialSet);

                additionalTags.clear();
                additionalTags.addAll(additionalTagSet);
            });
        } catch (TagMisconfigurationException ex) {
            SlimefunTranslation.log(Level.SEVERE, ex, "An error has occurred while trying to load SlimefunTranslationTag: " + name());
        }
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public boolean isTagged(@Nonnull Material material) {
        if (materials.contains(material)) {
            return true;
        } else {
            for (Tag<Material> tag : additionalTags) {
                if (tag.isTagged(material)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Nonnull
    @Override
    public Set<Material> getValues() {
        if (additionalTags.isEmpty()) {
            return Collections.unmodifiableSet(materials);
        } else {
            Set<Material> values = EnumSet.noneOf(Material.class);
            values.addAll(materials);

            for (Tag<Material> tag : additionalTags) {
                values.addAll(tag.getValues());
            }

            return values;
        }
    }

    public boolean isEmpty() {
        if (!materials.isEmpty()) {
            return false;
        } else {
            return getValues().isEmpty();
        }
    }
}
