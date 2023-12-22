package net.guizhanss.slimefuntranslation.implementation.managers;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import net.guizhanss.slimefuntranslation.api.TranslationConfiguration;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.interfaces.Translatable;
import net.guizhanss.slimefuntranslation.core.users.User;

public final class TranslationManager {
    private static final String FOLDER_NAME = "translations";

    public TranslationManager(SlimefunTranslation plugin) {
        File translationsFolder = new File(plugin.getDataFolder(), FOLDER_NAME);
        if (!translationsFolder.exists()) {
            translationsFolder.mkdirs();
        }
    }

    public void loadTranslations() {

    }

    private void loadFixedTranslations() {
        // TODO: Load file translations
    }

    private void loadProgrammedTranslations() {
        // TODO: Load Translatable SlimefunItem translations
    }

    /**
     * Register a {@link TranslationConfiguration}.
     * This can be called from the loading methods in this class, or by other plugins if they don't want to implement {@link Translatable}.
     * @param configuration The {@link TranslationConfiguration}.
     */
    public void registerTranslationConfiguration(@Nonnull TranslationConfiguration configuration) {

    }

    /**
     * Translate the given {@link ItemStack} for the given {@link User}.
     * The given {@link ItemStack} must have a Slimefun item, or the translation will not be applied.
     *
     * @param user
     *     The {@link User}.
     * @param item
     *     The {@link ItemStack}.
     *
     * @return Whether the item was translated.
     */
    public boolean translateItem(@Nonnull User user, @Nullable ItemStack item) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem == null) {
            return false;
        }

        return translateItem(user, item, sfItem);
    }

    @ParametersAreNonnullByDefault
    private boolean translateItem(User user, ItemStack item, SlimefunItem sfItem) {
        if (sfItem instanceof Translatable translatable) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                meta.setDisplayName(translatable.getTranslatedDisplayName(meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                meta.setLore(translatable.getTranslatedLore(meta.getLore()));
            }
            return true;
        }

        return false;
    }
}
