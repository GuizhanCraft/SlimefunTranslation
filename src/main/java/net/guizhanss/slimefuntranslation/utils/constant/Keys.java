package net.guizhanss.slimefuntranslation.utils.constant;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

import lombok.experimental.UtilityClass;

@SuppressWarnings("deprecation")
@UtilityClass
public final class Keys {
    public static final NamespacedKey SEARCH_DISPLAY = create("search_display");
    // slimefun keys
    public static final NamespacedKey SLIMEFUN_ITEM = new NamespacedKey("slimefun", "slimefun_item");
    public static final NamespacedKey SLIMEFUN_ITEM_GROUP = new NamespacedKey("slimefun", "item_group");
    // zAuctionHouse keys
    public static final NamespacedKey AUCTION_ITEM = new NamespacedKey("zauctionhousev3", "zauctionhouse-item");

    @Nonnull
    private static NamespacedKey create(@Nonnull String key) {
        return new NamespacedKey(SlimefunTranslation.getInstance(), key);
    }
}
