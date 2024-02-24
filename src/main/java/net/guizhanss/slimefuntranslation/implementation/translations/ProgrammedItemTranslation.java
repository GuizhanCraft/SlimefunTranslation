package net.guizhanss.slimefuntranslation.implementation.translations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.constant.Methods;

import lombok.RequiredArgsConstructor;

/**
 * This {@link ItemTranslation} is applied by {@link SlimefunItem}s which implemented the 2 methods to provide translations.
 */
@RequiredArgsConstructor
public class ProgrammedItemTranslation implements ItemTranslation {
    private final String lang;
    private final SlimefunItem sfItem;

    private int errorCount = 0;

    /**
     * Get the display name of the item.
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
        try {
            Method method = sfItem.getClass().getDeclaredMethod(Methods.TRANSLATABLE_ITEM_GET_NAME, Methods.TRANSLATABLE_ITEM_GET_NAME_PARAMS);
            Object obj = method.invoke(sfItem, user.getPlayer(), user.getLocale(), item, original);
            return (String) obj;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
            handleError(user, e);
            return original;
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getLore(User user, ItemStack item, ItemMeta meta, List<String> original) {
        try {
            Method method = sfItem.getClass().getDeclaredMethod(Methods.TRANSLATABLE_ITEM_GET_LORE, Methods.TRANSLATABLE_ITEM_GET_LORE_PARAMS);
            Object obj = method.invoke(sfItem, user.getPlayer(), user.getLocale(), item, original);
            return (List<String>) obj;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
            handleError(user, e);
            return original;
        }
    }

    @ParametersAreNonnullByDefault
    private void handleError(User user, Exception ex) {
        SlimefunTranslation.log(Level.SEVERE, ex, "An error has occurred while translating item " + sfItem.getId() + " for user " + user.getPlayer().getName() + "." +
            "Report this to the developer of addon " + sfItem.getAddon().getName() + ".");
        if (++errorCount >= 5) {
            for (var idMap : SlimefunTranslation.getRegistry().getItemTranslations().values()) {
                for (var entry : idMap.entrySet()) {
                    if (entry.getKey().equals(sfItem.getId())) {
                        idMap.remove(entry.getKey());
                    }
                }
            }
            SlimefunTranslation.log(Level.SEVERE, "Too many exceptions were thrown by item translation of " + sfItem.getId() + " for user " + user.getPlayer().getName() + "." +
                "Terminating translation.");
        }
    }
}
