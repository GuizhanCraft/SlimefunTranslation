package net.guizhanss.slimefuntranslation.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.guizhanss.slimefuntranslation.utils.constant.Keys;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;

import lombok.experimental.UtilityClass;

import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
@UtilityClass
public class SlimefunItemUtils {
    private static final NamespacedKey ID_KEY = new NamespacedKey("slimefun", "slimefun_item");

    @Nullable
    public static String getId(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        if (item instanceof SlimefunItemStack sfItemStack) {
            return sfItemStack.getItemId();
        } else {
            return PersistentDataAPI.getString(item.getItemMeta(), ID_KEY);
        }
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
