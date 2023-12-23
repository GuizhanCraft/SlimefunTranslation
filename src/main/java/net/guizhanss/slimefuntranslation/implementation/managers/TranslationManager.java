package net.guizhanss.slimefuntranslation.implementation.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import net.guizhanss.guizhanlib.minecraft.utils.ChatUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.guizhanlib.slimefun.addon.AddonConfig;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.TranslationConfiguration;
import net.guizhanss.slimefuntranslation.api.interfaces.Translatable;
import net.guizhanss.slimefuntranslation.api.interfaces.Translation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.FileUtils;

public final class TranslationManager {
    private static final String FOLDER_NAME = "translations";
    private static final String DEFAULT_LANGUAGE = "en";
    private final File translationsFolder;

    @ParametersAreNonnullByDefault
    public TranslationManager(SlimefunTranslation plugin, File jarFile) {
        translationsFolder = new File(plugin.getDataFolder(), FOLDER_NAME);
        if (!translationsFolder.exists()) {
            translationsFolder.mkdirs();

            // also unzip the example translations
            List<String> translationFiles = FileUtils.listYamlFilesInJar(jarFile, FOLDER_NAME + File.separator);
            for (String translationFile : translationFiles) {
                plugin.saveResource(FOLDER_NAME + File.separator + translationFile, false);
            }
        }
    }

    public void loadTranslations() {
        loadFixedTranslations();
        loadProgrammedTranslations();
    }

    private void loadFixedTranslations() {
        List<String> translationFiles = FileUtils.listYamlFiles(translationsFolder);
        for (String translationFile : translationFiles) {
            var config = YamlConfiguration.loadConfiguration(new File(translationsFolder, translationFile));
            var translationConfig = TranslationConfiguration.fromFileConfiguration(config);
            if (translationConfig.isEmpty()) {
                continue;
            }
            translationConfig.get().register(SlimefunTranslation.getInstance());
        }

    }

    private void loadProgrammedTranslations() {
        for (SlimefunItem sfItem : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (!(sfItem instanceof Translatable translatable)) {
                continue;
            }
            // TODO: complete this

        }
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
        final Translation translation = findItemTranslation(user, item, sfItem.getId());
        if (translation == null) {
            return false;
        }

        if (!translation.canTranslate(item, sfItem)) {
            return false;
        }

        final ItemMeta meta = item.getItemMeta();
        String originalDisplayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        meta.setDisplayName(ChatUtil.color(translation.getDisplayName(originalDisplayName)));
        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        meta.setLore(ChatUtil.color(translation.getLore(originalLore)));
        item.setItemMeta(meta);
        return true;
    }

    @Nullable
    @ParametersAreNonnullByDefault
    private Translation findItemTranslation(User user, ItemStack item, String id) {
        SlimefunTranslation.debug("Attempting to find the translation for item {0} for user {1}", id, user.getPlayer().getName());
        var allTranslations = SlimefunTranslation.getRegistry().getTranslations();
        // find the translations for user's current locale
        var translations = allTranslations.get(user.getLocale());
        if (translations != null) {
            // then find the translation for the item
            var translation = translations.get(id);
            if (translation != null) {
                return translation;
            }
        }

        SlimefunTranslation.debug("User's locale {0} does not have translation for item.", user.getLocale());

        // user's current locale does not have translation for the given item,
        // try server default locale
        var serverDefault = Slimefun.getLocalization().getDefaultLanguage();
        if (serverDefault != null) {
            translations = allTranslations.get(serverDefault.getId());
            if (translations != null) {
                var translation = translations.get(id);
                if (translation != null) {
                    return translation;
                }
            }
        }

        SlimefunTranslation.debug("Server default locale {0} does not have translation for item.", serverDefault);

        // try english at last
        translations = allTranslations.get(DEFAULT_LANGUAGE);
        if (translations != null) {
            return translations.get(id);
        }

        SlimefunTranslation.debug("English does not have translation for item.");

        return null;
    }
}
