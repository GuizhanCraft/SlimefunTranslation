package net.guizhanss.slimefuntranslation.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;
import net.guizhanss.slimefuntranslation.implementation.translations.FixedItemTranslation;
import net.guizhanss.slimefuntranslation.utils.ConfigUtils;

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
@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class TranslationConfiguration {
    private final String name;
    private final String lang;
    private final Map<String, ItemTranslation> itemTranslations;
    private final Map<String, String> loreTranslations;
    private final Map<String, String> messageTranslations;

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
        return fromFileConfiguration(language, config, TranslationConfigurationFields.DEFAULT);
    }

    /**
     * Creates a {@link TranslationConfiguration} from a {@link FileConfiguration} with the given {@link TranslationConfigurationFields}.
     *
     * @param language the language of the translation.
     * @param config   the {@link FileConfiguration} to create the {@link TranslationConfiguration} from.
     * @param fields   the fields to look for in the config.
     * @return an {@link Optional} of {@link TranslationConfiguration} if the config is valid, otherwise {@code null}.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static Optional<TranslationConfiguration> fromFileConfiguration(
        String language,
        FileConfiguration config,
        TranslationConfigurationFields fields
    ) {
        Preconditions.checkArgument(config != null, "config cannot be null");

        String name = config.getString("name", "Unnamed Translation");
        String lang = SlimefunTranslation.getConfigService().getMappedLanguage(language);
        String prefix = config.getString(fields.getPrefix(), "");
        String suffix = config.getString(fields.getSuffix(), "");

        var itemsSection = config.getConfigurationSection(fields.getItems());
        var loreSection = config.getConfigurationSection(fields.getLore());
        var messagesSection = config.getConfigurationSection(fields.getMessages());
        if (itemsSection == null && loreSection == null && messagesSection == null) {
            SlimefunTranslation.log(Level.WARNING, "No translations found in " + name);
            return Optional.empty();
        }
        var fileConditions = TranslationConditions.load(config.getConfigurationSection(fields.getConditions()));
        SlimefunTranslation.debug("Current file condition: {0}", fileConditions);
        Map<String, ItemTranslation> itemTranslations = new HashMap<>();
        Map<String, String> loreTranslations = new HashMap<>();
        Map<String, String> messageTranslations = new HashMap<>();

        SlimefunTranslation.log(Level.INFO, "Loading translation configuration \"{0}\", language: {1}", name, lang);

        if (itemsSection != null) {
            int count = 0;
            for (var id : itemsSection.getKeys(false)) {
                String itemId = prefix + id + suffix;
                SlimefunTranslation.debug("Loading item translation {0}", itemId);

                var itemSection = itemsSection.getConfigurationSection(itemId);
                if (itemSection == null) {
                    SlimefunTranslation.log(Level.SEVERE, "Invalid item {0} in translation {1}", itemId, name);
                    continue;
                }

                var itemConditions = TranslationConditions.load(fileConditions, itemSection.getConfigurationSection(fields.getConditions()));
                SlimefunTranslation.debug("Current item condition: {0}", itemConditions);
                boolean forceLoad = itemConditions.isForceLoad();

                // sfItem
                SlimefunItem sfItem = SlimefunItem.getById(itemId);
                if (sfItem == null && !forceLoad) {
                    SlimefunTranslation.log(Level.SEVERE, "Invalid item {0}", itemId);
                    continue;
                }

                // name
                String displayName = "";
                if (itemSection.contains("name")) {
                    displayName = itemSection.getString("name", "");
                }

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
                        SlimefunTranslation.log(Level.SEVERE, "Invalid lore overrides of item {0} in translation {1}", itemId, name);
                        return Optional.empty();
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
                        SlimefunTranslation.log(Level.SEVERE, "Invalid lore replacements of item {0} in translation {1}", itemId, name);
                        return Optional.empty();
                    }
                }

                // check partial override
                if (!itemConditions.isPartialOverride() && !forceLoad) {
                    itemConditions.setPartialOverride(
                        SlimefunTranslation.getConfigService().getPartialOverrideMaterials().contains(sfItem.getItem().getType())
                    );
                }

                var translation = new FixedItemTranslation(displayName, lore, overrides, replacements, itemConditions);
                itemTranslations.put(itemId, translation);
                count++;
            }
            SlimefunTranslation.log(Level.INFO, "Loaded {0} item translations.", count);
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

        return Optional.of(new TranslationConfiguration(name, lang, itemTranslations, loreTranslations, messageTranslations));
    }

    public void register(@Nonnull SlimefunAddon addon) {
        if (state != State.UNREGISTERED) {
            throw new IllegalStateException("TranslationConfiguration is already registered");
        }

        var allItemTranslations = SlimefunTranslation.getRegistry().getItemTranslations();
        allItemTranslations.putIfAbsent(lang, new HashMap<>());
        var currentTranslations = allItemTranslations.get(lang);
        currentTranslations.putAll(itemTranslations);

        var allLoreTranslations = SlimefunTranslation.getRegistry().getLoreTranslations();
        allLoreTranslations.putIfAbsent(lang, new HashMap<>());
        var currentLoreTranslations = allLoreTranslations.get(lang);
        currentLoreTranslations.putAll(loreTranslations);

        var allMessageTranslations = SlimefunTranslation.getRegistry().getMessageTranslations();
        allMessageTranslations.putIfAbsent(addon.getName(), new HashMap<>());
        var pluginMessageTranslations = allMessageTranslations.get(addon.getName());
        pluginMessageTranslations.putIfAbsent(lang, new HashMap<>());
        var currentMessageTranslations = pluginMessageTranslations.get(lang);
        currentMessageTranslations.putAll(messageTranslations);

        this.addon = addon;
        setState(State.REGISTERED);
    }

    public enum State {
        UNREGISTERED,
        REGISTERED
    }
}
