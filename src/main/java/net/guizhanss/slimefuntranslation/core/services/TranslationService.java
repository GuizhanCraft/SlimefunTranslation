package net.guizhanss.slimefuntranslation.core.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import net.guizhanss.guizhanlib.minecraft.utils.ChatUtil;

import net.md_5.bungee.api.ChatMessageType;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
            SlimefunTranslation.debug("translations folder not exist, extracting default translations");
            List<String> translationFiles = FileUtils.listYamlFilesInJar(jarFile, FOLDER_NAME + "/");
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
            itemTranslation.getDisplayName(sfItem.getItemName())
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
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem == null) {
            return false;
        }

        return translateItem(user, item, sfItem);
    }

    @ParametersAreNonnullByDefault
    private boolean translateItem(User user, ItemStack item, SlimefunItem sfItem) {
        // check if the item is disabled
        if (SlimefunTranslation.getConfigService().getDisabledItems().contains(sfItem.getId())) {
            return false;
        }
        // find the translation
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getItemTranslations(), user, sfItem.getId());
        if (transl.isEmpty()) {
            return false;
        }
        var translation = transl.get();

        // check whether the translation can be applied
        if (!translation.canTranslate(item, sfItem)) {
            return false;
        }

        var integrationService = SlimefunTranslation.getIntegrationService();
        final ItemMeta meta = item.getItemMeta();
        // display name
        String originalDisplayName = meta.hasDisplayName() ? meta.getDisplayName() : "";
        meta.setDisplayName(integrationService.applyPlaceholders(user, translation.getDisplayName(originalDisplayName)));
        // lore
        List<String> originalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        meta.setLore(integrationService.applyPlaceholders(user, translation.getLore(originalLore)));

        item.setItemMeta(meta);
        return true;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public String translateLore(User user, String id) {
        return translateLore(user, id, false);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public String translateLore(User user, String id, boolean defaultToId) {
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getLoreTranslations(), user, id);
        return transl.orElse(defaultToId ? id : "");
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender sender, String key) {
        Preconditions.checkArgument(sender != null, "sender cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");
        User user = null;
        if (sender instanceof Player p) {
            user = SlimefunTranslation.getUserService().getUser(p);
        }
        sender.sendMessage(getMessage(key, user));
    }

    @ParametersAreNonnullByDefault
    public void sendActionbarMessage(User user, String key) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");
        user.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getMessage(key, user)));
    }

    @Nonnull
    private String getMessage(@Nonnull String key, @Nullable User user) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getMessageTranslations(), user, key);
        if (transl.isEmpty()) {
            return key;
        }

        String message = ChatUtil.color(transl.get());
        if (user != null) {
            message = SlimefunTranslation.getIntegrationService().applyPlaceholders(user, message);
        }
        return message;
    }
}
