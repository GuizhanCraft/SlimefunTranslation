package net.guizhanss.slimefuntranslation.core.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.config.TranslationConfiguration;
import net.guizhanss.slimefuntranslation.api.interfaces.TranslatableItem;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.implementation.translations.ProgrammedItemTranslation;
import net.guizhanss.slimefuntranslation.utils.FileUtils;
import net.guizhanss.slimefuntranslation.utils.TranslationUtils;

@SuppressWarnings("ConstantConditions")
public final class TranslationService {
    private static final String FOLDER_NAME = "translations";
    private final File translationsFolder;

    @ParametersAreNonnullByDefault
    public TranslationService(SlimefunTranslation plugin, File jarFile) {
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
        loadLanguages();
        loadFixedTranslations();
        loadProgrammedTranslations();
    }

    private void loadLanguages() {
        List<String> languages = FileUtils.listFolders(translationsFolder);
        SlimefunTranslation.getRegistry().getLanguages().addAll(languages);
    }

    private void loadFixedTranslations() {
        for (String lang : SlimefunTranslation.getRegistry().getLanguages()) {
            File languageFolder = new File(translationsFolder, lang);
            List<String> translationFiles = FileUtils.listYamlFiles(languageFolder);
            for (String translationFile : translationFiles) {
                var config = YamlConfiguration.loadConfiguration(new File(languageFolder, translationFile));
                var translationConfig = TranslationConfiguration.fromFileConfiguration(lang, config);
                if (translationConfig.isEmpty()) {
                    continue;
                }
                translationConfig.get().register(SlimefunTranslation.getInstance());
            }
        }
    }

    private void loadProgrammedTranslations() {
        var languages = SlimefunTranslation.getRegistry().getLanguages();
        for (SlimefunItem sfItem : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (!(sfItem instanceof TranslatableItem translatableItem)) {
                continue;
            }
            for (String lang : languages) {
                var translation = new ProgrammedItemTranslation(lang, translatableItem);
                var allItemTranslations = SlimefunTranslation.getRegistry().getItemTranslations();
                allItemTranslations.putIfAbsent(lang, new HashMap<>());
                var currentTranslations = allItemTranslations.get(lang);
                currentTranslations.put(sfItem.getId(), translation);
            }
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
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getItemTranslations(), user, sfItem.getId());
        if (transl.isEmpty()) {
            return false;
        }
        var translation = transl.get();

        if (!translation.canTranslate(item, sfItem)) {
            return false;
        }

        var integrationService = SlimefunTranslation.getIntegrationService();
        final ItemMeta meta = item.getItemMeta();
        String originalDisplayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        meta.setDisplayName(integrationService.applyPlaceholders(user, translation.getDisplayName(originalDisplayName)));
        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        meta.setLore(integrationService.applyPlaceholders(user, translation.getLore(originalLore)));
        item.setItemMeta(meta);
        return true;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public String translateLore(User user, String id) {
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getLoreTranslations(), user, id);
        return transl.orElse("");
    }
}
