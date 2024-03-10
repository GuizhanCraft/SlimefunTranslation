package net.guizhanss.slimefuntranslation.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;
import net.guizhanss.slimefuntranslation.implementation.translations.FixedItemTranslation;
import net.guizhanss.slimefuntranslation.utils.ConfigUtils;
import net.guizhanss.slimefuntranslation.utils.GeneralUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This class holds the information provided from a valid translations file, or from other addons.
 *
 * @author ybw0014
 */
@SuppressWarnings("ConstantConditions")
@Getter
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TranslationConfiguration {
    private final String name;
    private final String lang;
    private final Translations translations;

    private State state = State.UNREGISTERED;
    private SlimefunAddon addon = null;

    /**
     * Creates a {@link TranslationConfiguration} from a {@link FileConfiguration} with default {@link TranslationConfigurationFields}.
     *
     * @param language the language of the translation.
     * @param config   the {@link FileConfiguration} to create the {@link TranslationConfiguration} from.
     * @return an {@link Optional} of {@link TranslationConfiguration} if the config is valid, otherwise {@code null}.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static Optional<TranslationConfiguration> fromFileConfiguration(String language, FileConfiguration config) {
        return fromFileConfiguration(language, config, TranslationConfigurationFields.DEFAULT, TranslationConfigurationDefaults.DEFAULT);
    }

    /**
     * Creates a {@link TranslationConfiguration} from a {@link FileConfiguration} with the given {@link TranslationConfigurationFields}.
     *
     * @param language the language of the translation.
     * @param config   the {@link FileConfiguration} to create the {@link TranslationConfiguration} from.
     * @param fields   the fields to look for in the config.
     * @param defaults the default values for the fields.
     * @return an {@link Optional} of {@link TranslationConfiguration} if the config is valid, otherwise {@code null}.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static Optional<TranslationConfiguration> fromFileConfiguration(
        String language,
        FileConfiguration config,
        TranslationConfigurationFields fields,
        TranslationConfigurationDefaults defaults
    ) {
        Preconditions.checkArgument(config != null, "config cannot be null");

        String name = config.getString("name", defaults.getName());
        String lang = SlimefunTranslation.getConfigService().getMappedLanguage(language);
        String itemIdPrefix = config.getString(fields.getPrefix(), defaults.getPrefix());
        String itemIdSuffix = config.getString(fields.getSuffix(), defaults.getSuffix());

        var guidesSection = config.getConfigurationSection(fields.getGuides());
        var itemGroupsSection = config.getConfigurationSection(fields.getItemGroups());
        var itemsSection = config.getConfigurationSection(fields.getItems());
        var loreSection = config.getConfigurationSection(fields.getLore());
        var messagesSection = config.getConfigurationSection(fields.getMessages());

        if (GeneralUtils.isAllNull(guidesSection, itemGroupsSection, itemsSection, loreSection, messagesSection)) {
            SlimefunTranslation.log(Level.WARNING, "No translations found in " + name);
            return Optional.empty();
        }

        var fileConditions = TranslationConditions.load(config.getConfigurationSection(fields.getConditions()));
        SlimefunTranslation.debug("Current file condition: {0}", fileConditions);
        var translations = new Translations();
        var guideTranslations = translations.getGuide();
        var itemGroupTranslations = translations.getItemGroup();
        var itemTranslations = translations.getItem();
        var loreTranslations = translations.getLore();
        var messageTranslations = translations.getMessage();

        SlimefunTranslation.log(Level.INFO, "Loading translation configuration \"{0}\", language: {1}", name, lang);

        if (guidesSection != null) {
            loadGuides(guideTranslations, guidesSection);
        }

        if (itemGroupsSection != null) {
            for (var group : itemGroupsSection.getKeys(true)) {
                SlimefunTranslation.debug("Loading item group translation {0}", group);
                var translation = itemGroupsSection.getString(group);
                itemGroupTranslations.put(group, translation);
            }
        }

        if (itemsSection != null) {
            loadItems(itemTranslations, itemsSection, fields, fileConditions, itemIdPrefix, itemIdSuffix);
        }

        if (loreSection != null) {
            for (var loreId : loreSection.getKeys(true)) {
                SlimefunTranslation.debug("Loading lore translation {0}", loreId);
                var lore = loreSection.getString(loreId);
                loreTranslations.put(loreId, lore);
            }
        }

        if (messagesSection != null) {
            for (var messageId : messagesSection.getKeys(true)) {
                SlimefunTranslation.debug("Loading message translation {0}", messageId);
                var message = messagesSection.getString(messageId);
                messageTranslations.put(messageId, message);
            }
        }

        return Optional.of(new TranslationConfiguration(name, lang, translations));
    }

    private static void loadGuides(
        Map<String, ItemTranslation> translations,
        ConfigurationSection guidesSection
    ) {
        for (var mode : guidesSection.getKeys(false)) {
            SlimefunTranslation.debug("Loading guide translation for mode {0}", mode);

            var guideSection = guidesSection.getConfigurationSection(mode);
            if (guideSection == null) {
                SlimefunTranslation.log(Level.SEVERE, "Invalid guide section {0} in configuration file.", mode);
                continue;
            }

            // name
            String displayName = guideSection.getString("name", "");
            // lore
            var lore = guideSection.getStringList("lore");

            var translation = new FixedItemTranslation(displayName, lore);
            translations.put(mode, translation);
        }
    }

    private static void loadItems(
        Map<String, ItemTranslation> translations,
        ConfigurationSection itemsSection,
        TranslationConfigurationFields fields,
        TranslationConditions fileConditions,
        String prefix,
        String suffix
    ) {
        int count = 0;
        for (var id : itemsSection.getKeys(false)) {
            String itemId = prefix + id + suffix;
            SlimefunTranslation.debug("Loading item translation {0}", itemId);

            var itemSection = itemsSection.getConfigurationSection(id);
            if (itemSection == null) {
                SlimefunTranslation.log(Level.SEVERE, "Invalid item section {0} in configuration file.", id);
                continue;
            }

            var itemConditions = TranslationConditions.load(fileConditions, itemSection.getConfigurationSection(fields.getConditions()));
            SlimefunTranslation.debug("Current item condition: {0}", itemConditions);
            boolean forceLoad = itemConditions.isForceLoad();

            // sfItem
            SlimefunItem sfItem = SlimefunItem.getById(itemId);
            if (sfItem == null && !forceLoad) {
                SlimefunTranslation.log(Level.WARNING, "Item {0} is not registered, ignoring.", itemId);
                continue;
            }

            // name
            String displayName = itemSection.getString("name", "");

            // lore
            var lore = itemSection.getStringList("lore");

            // lore overrides
            Map<Integer, String> overrides = new HashMap<>();
            if (itemSection.contains("lore-overrides")) {
                try {
                    Map<String, String> replacements = ConfigUtils.getMap(itemSection.getConfigurationSection("lore-overrides"));
                    for (var entry : replacements.entrySet()) {
                        overrides.put(Integer.parseInt(entry.getKey()), entry.getValue());
                    }
                } catch (NumberFormatException | NullPointerException ex) {
                    SlimefunTranslation.log(Level.SEVERE, "Invalid lore overrides of item {0}.", itemId);
                    continue;
                }
            }

            // lore replacements
            Map<Integer, Pair<String, String>> replacements = new HashMap<>();
            if (itemSection.contains("lore-replacements")) {
                try {
                    for (String idx : itemSection.getConfigurationSection("lore-replacements").getKeys(false)) {
                        int i = Integer.parseInt(idx);
                        var section = itemSection.getConfigurationSection("lore-replacements." + idx);
                        replacements.put(i, new Pair<>(section.getString("original"), section.getString("replaced")));
                    }
                } catch (NumberFormatException | NullPointerException ex) {
                    SlimefunTranslation.log(Level.SEVERE, "Invalid lore replacements of item {0}", itemId);
                    continue;
                }
            }

            // check partial override
            if (!itemConditions.isPartialOverride() && !forceLoad) {
                itemConditions.setPartialOverride(
                    SlimefunTranslation.getConfigService().getPartialOverrideMaterials().contains(sfItem.getItem().getType())
                );
            }

            var translation = new FixedItemTranslation(displayName, lore, overrides, replacements, itemConditions);
            translations.put(itemId, translation);
            count++;
        }
        SlimefunTranslation.debug("Loaded {0} item translations.", count);
    }

    public void register(@Nonnull SlimefunAddon addon) {
        if (state != State.UNREGISTERED) {
            throw new IllegalStateException("TranslationConfiguration is already registered");
        }

        var registry = SlimefunTranslation.getRegistry();

        // guides
        registerTranslations(registry.getGuideTranslations(), lang, translations.getGuide());
        // itemGroups
        registerTranslations(registry.getItemGroupTranslations(), lang, translations.getItemGroup());
        // items
        registerTranslations(registry.getItemTranslations(), lang, translations.getItem());
        // lore
        registerTranslations(registry.getLoreTranslations(), lang, translations.getLore());
        // messages
        var allMessageTranslations = registry.getMessageTranslations();
        var pluginMessageTranslations = allMessageTranslations.computeIfAbsent(addon.getName(), k -> new HashMap<>());
        registerTranslations(pluginMessageTranslations, lang, translations.getMessage());

        setAddon(addon);
        setState(State.REGISTERED);
    }

    private <V> void registerTranslations(Map<String, Map<String, V>> allTranslations, String lang, Map<String, V> translations) {
        var currentTranslations = allTranslations.computeIfAbsent(lang, k -> new HashMap<>());
        currentTranslations.putAll(translations);
    }

    public enum State {
        UNREGISTERED,
        REGISTERED
    }
}
