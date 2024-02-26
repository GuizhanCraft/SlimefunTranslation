package net.guizhanss.slimefuntranslation.core.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.config.TranslationConfiguration;
import net.guizhanss.slimefuntranslation.api.events.TranslationsLoadEvent;
import net.guizhanss.slimefuntranslation.api.translation.TranslationStatus;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.implementation.translations.ProgrammedItemTranslation;
import net.guizhanss.slimefuntranslation.utils.ColorUtils;
import net.guizhanss.slimefuntranslation.utils.FileUtils;
import net.guizhanss.slimefuntranslation.utils.SlimefunItemUtils;
import net.guizhanss.slimefuntranslation.utils.TranslationUtils;
import net.guizhanss.slimefuntranslation.utils.constant.Keys;

/**
 * This class holds all translations and can be used to access these translations
 * by calling {@link SlimefunTranslation#getTranslationService()}.
 *
 * @author ybw0014
 */
@SuppressWarnings("ConstantConditions")
public final class TranslationService {
    private static final String FOLDER_NAME = "translations";
    private final SlimefunTranslation plugin;
    private final File translationsFolder;
    private final File jarFile;

    @ParametersAreNonnullByDefault
    public TranslationService(SlimefunTranslation plugin, File jarFile) {
        translationsFolder = new File(plugin.getDataFolder(), FOLDER_NAME);
        this.plugin = plugin;
        this.jarFile = jarFile;

        extractTranslations(false);
    }

    public void callLoadEvent() {
        Bukkit.getPluginManager().callEvent(new TranslationsLoadEvent());
    }

    /**
     * Loads all translation. Other plugins should never call this method.
     * <p>
     * This should be called after all items are loaded.
     */
    public void loadTranslations() {
        loadLanguages();
        loadFixedTranslations();
        loadProgrammedTranslations();
    }

    public void clearTranslations() {
        var registry = SlimefunTranslation.getRegistry();
        registry.getLanguages().clear();
        registry.getItemTranslations().clear();
        registry.getLoreTranslations().clear();
        registry.getMessageTranslations().clear();
    }

    private void loadLanguages() {
        List<String> languages = FileUtils.listFolders(translationsFolder);
        SlimefunTranslation.getRegistry().getLanguages().addAll(languages);
    }

    private void loadFixedTranslations() {
        // standard translations
        for (String lang : SlimefunTranslation.getRegistry().getLanguages()) {
            loadFixedTranslations(lang);
        }
        // language mappings
        for (String lang : SlimefunTranslation.getConfigService().getLanguageMappings().keySet()) {
            loadFixedTranslations(lang);
        }
    }

    private void loadFixedTranslations(String language) {
        File languageFolder = new File(translationsFolder, language);
        List<String> translationFiles = FileUtils.listYamlFiles(languageFolder);
        for (String translationFile : translationFiles) {
            var config = YamlConfiguration.loadConfiguration(new File(languageFolder, translationFile));
            var translationConfig = TranslationConfiguration.fromFileConfiguration(language, config);
            if (translationConfig.isEmpty()) {
                continue;
            }
            translationConfig.get().register(SlimefunTranslation.getInstance());
        }
    }

    private void loadProgrammedTranslations() {
        var languages = SlimefunTranslation.getRegistry().getLanguages();
        for (SlimefunItem sfItem : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (!TranslationUtils.isTranslatableItem(sfItem)) {
                continue;
            }
            for (String lang : languages) {
                var translation = new ProgrammedItemTranslation(lang, sfItem);
                var allItemTranslations = SlimefunTranslation.getRegistry().getItemTranslations();
                allItemTranslations.putIfAbsent(lang, new HashMap<>());
                var currentTranslations = allItemTranslations.get(lang);
                currentTranslations.put(sfItem.getId(), translation);
            }
        }
    }

    /**
     * Extracts all translation files from the jar file.
     *
     * @param replace Whether to replace existing files.
     */
    public void extractTranslations(boolean replace) {
        if (!translationsFolder.exists()) {
            translationsFolder.mkdirs();
        }
        List<String> translationFiles = FileUtils.listYamlFilesInJar(jarFile, FOLDER_NAME + "/");
        for (String translationFile : translationFiles) {
            String filePath = FOLDER_NAME + File.separator + translationFile;
            File file = new File(plugin.getDataFolder(), filePath);
            if (file.exists() && !replace) {
                continue;
            }
            plugin.saveResource(filePath, replace);
        }
    }

    /**
     * Exports item translations to a file in the specified language.
     *
     * @param language  The language for which to export the translations.
     * @param addonName The name of the addon.
     * @param ids       The set of item ids to export translations for.
     * @return The name of the exported file.
     */
    @ParametersAreNonnullByDefault
    public String exportItemTranslations(String language, String addonName, Set<String> ids) {
        // make language folder if not exists
        File langFolder = new File(translationsFolder, language);
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // find the next available file name
        int idx = 1;
        File file;
        String fileName;
        do {
            fileName = "export-" + idx + ".yml";
            file = new File(langFolder, fileName);
            idx++;
        } while (file.exists());

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", addonName);
        for (String itemId : ids) {
            String path = "translations." + itemId;
            SlimefunItem sfItem = SlimefunItem.getById(itemId);
            if (sfItem == null) {
                continue;
            }
            config.set(path + ".name", ColorUtils.useAltCode(sfItem.getItemName()));
            var meta = sfItem.getItem().getItemMeta();
            if (meta.hasLore()) {
                config.set(path + ".lore", ColorUtils.useAltCode(meta.getLore()));
            }
        }

        // save the file
        try {
            config.save(file);
        } catch (IOException ex) {
            SlimefunTranslation.log(Level.SEVERE, ex, "An error has occurred while exporting translation file.");
        }

        return fileName;
    }

    /**
     * Get the translated item name of {@link SlimefunItem} for the given {@link User}.
     *
     * @param user   The {@link User}.
     * @param sfItem The {@link SlimefunItem}.
     * @return The translated name. Will be an empty string if the item is invalid.
     * Or be the original name if there is no available translation.
     */
    @Nonnull
    public String getTranslatedItemName(@Nonnull User user, @Nullable SlimefunItem sfItem) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        if (sfItem == null) {
            return "";
        }
        // if the item is disabled, return the original name
        if (SlimefunTranslation.getConfigService().getDisabledItems().contains(sfItem.getId())) {
            return sfItem.getItemName();
        }
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getItemTranslations(), user, sfItem.getId());
        return transl.map(itemTranslation -> SlimefunTranslation.getIntegrationService().applyPlaceholders(
            user,
            itemTranslation.getDisplayName(user, sfItem.getItem(), sfItem.getItem().getItemMeta(), sfItem.getItemName())
        )).orElseGet(sfItem::getItemName);
    }

    /**
     * Translate the given {@link ItemStack} for the given {@link User}.
     * The given {@link ItemStack} must be a Slimefun item, or the translation will not be applied.
     *
     * @param user The {@link User}.
     * @param item The {@link ItemStack}.
     * @return Whether the item was translated.
     */
    public boolean translateItem(@Nonnull User user, @Nullable ItemStack item) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        String sfId = SlimefunItemUtils.getId(item);
        if (sfId == null) {
            return false;
        }

        return translateItem(user, item, sfId);
    }

    @ParametersAreNonnullByDefault
    private boolean translateItem(User user, ItemStack item, String sfId) {
        // check if the item is disabled
        if (SlimefunTranslation.getConfigService().getDisabledItems().contains(sfId)) {
            return false;
        }
        // find the translation
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getItemTranslations(), user, sfId);
        if (transl.isEmpty()) {
            return false;
        }
        var translation = transl.get();
        final ItemMeta meta = item.getItemMeta();

        // check whether the translation can be applied
        TranslationStatus status = translation.canTranslate(user, item, meta, sfId);
        if (status == TranslationStatus.DENIED) {
            return false;
        }

        var integrationService = SlimefunTranslation.getIntegrationService();
        // display name
        String originalDisplayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        meta.setDisplayName(integrationService.applyPlaceholders(user, translation.getDisplayName(user, item, meta, originalDisplayName)));
        // lore
        if (shouldTranslateLore(meta) && status != TranslationStatus.NAME_ONLY) {
            List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            meta.setLore(integrationService.applyPlaceholders(user, translation.getLore(user, item, meta, originalLore)));
        }

        item.setItemMeta(meta);
        return true;
    }

    @ParametersAreNonnullByDefault
    private boolean shouldTranslateLore(ItemMeta meta) {
        Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();
        return !keys.contains(Keys.SEARCH_DISPLAY)
            && !keys.contains(Keys.AUCTION_ITEM);
    }

    /**
     * Get the lore translation for the given {@link User}.
     *
     * @param user The {@link User}.
     * @param id   The id of the lore.
     * @return The translated lore. Will be an empty string if translation does not exist.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getLore(User user, String id) {
        return getLore(user, id, false);
    }

    /**
     * Get the lore translation for the given {@link User}.
     *
     * @param user        The {@link User}.
     * @param id          The id of the lore.
     * @param defaultToId Whether to return the id if the translation does not exist.
     * @return The translated lore. Will return either id or empty string based on {@code defaultToId}.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getLore(User user, String id, boolean defaultToId) {
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getLoreTranslations(), user, id);
        return transl.orElse(defaultToId ? id : "");
    }
}
