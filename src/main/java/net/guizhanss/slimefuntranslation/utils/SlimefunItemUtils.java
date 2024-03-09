package net.guizhanss.slimefuntranslation.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;

import net.guizhanss.slimefuntranslation.utils.constant.Keys;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SlimefunItemUtils {

    @Nullable
    public static String getId(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        if (item instanceof SlimefunItemStack sfItemStack) {
            return sfItemStack.getItemId();
        } else {
            return PersistentDataAPI.getString(item.getItemMeta(), Keys.SLIMEFUN_ITEM);
        }
    }

    @Nullable
    public static SlimefunGuideMode getGuideMode(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        String mode = PersistentDataAPI.getString(item.getItemMeta(), Slimefun.getRegistry().getGuideDataKey());
        try {
            return SlimefunGuideMode.valueOf(mode);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public static String getItemGroupKey(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        return PersistentDataAPI.getString(item.getItemMeta(), Keys.SLIMEFUN_ITEM_GROUP);
    }

    @Nonnull
    public static ItemStack getDisplayItem(@Nonnull ItemStack item) {
        ItemStack displayItem = item.clone();
        ItemMeta meta = displayItem.getItemMeta();
        PersistentDataAPI.setBoolean(meta, Keys.SEARCH_DISPLAY, true);
        displayItem.setItemMeta(meta);
        return displayItem;
    }
}
