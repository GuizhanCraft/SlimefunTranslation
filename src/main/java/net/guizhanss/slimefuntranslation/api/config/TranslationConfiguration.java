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
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.interfaces.ItemTranslation;
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
    private final String author;
    private final String lang;
    private final Map<String, ItemTranslation> itemTranslations;
    private final Map<String, String> loreTranslations;

    private State state = State.UNREGISTERED;
    private SlimefunAddon addon = null;

    /**
     * Creates a {@link TranslationConfiguration} from a {@link FileConfiguration}.
     *
     * @param language
     *     the language of the translation.
     * @param config
     *     the {@link FileConfiguration} to create the {@link TranslationConfiguration} from.
     *
     * @return an {@link Optional} of {@link TranslationConfiguration} if the config is valid, otherwise {@code null}.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static Optional<TranslationConfiguration> fromFileConfiguration(String language, FileConfiguration config) {
        Preconditions.checkArgument(config != null, "config cannot be null");

        String name = config.getString("name", "Unnamed Translation");
        String author = config.getString("author", "SlimefunTranslation");
        String lang = SlimefunTranslation.getConfigService().getMappedLanguage(language);

        var itemsSection = config.getConfigurationSection("translations");
        var loreSection = config.getConfigurationSection("lore");
        if (itemsSection == null && loreSection == null) {
            SlimefunTranslation.log(Level.WARNING, "No translations found in " + name + " by " + author);
            return Optional.empty();
        }
        Map<String, ItemTranslation> itemTranslations = new HashMap<>();
        Map<String, String> loreTranslations = new HashMap<>();

        SlimefunTranslation.log(Level.INFO, "Loading translation configuration \"{0}\" by {1}, language: {2}", name, author, lang);

        if (itemsSection != null) {
            for (var itemId : itemsSection.getKeys(false)) {
                SlimefunTranslation.debug("Loading item translation {0}", itemId);
                var itemSection = itemsSection.getConfigurationSection(itemId);
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
                        SlimefunTranslation.log(Level.SEVERE, "Invalid lore overrides of item {0} in translation {1} by {2}", itemId, name, author);
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
                        SlimefunTranslation.log(Level.SEVERE, "Invalid lore replacements of item {0} in translation {1} by {2}", itemId, name, author);
                        return Optional.empty();
                    }
                }

                // check name
                boolean checkName = itemSection.getBoolean("check-name", false);

                var translation = new FixedItemTranslation(displayName, lore, overrides, replacements, checkName);
                itemTranslations.put(itemId, translation);
            }
        }

        if (loreSection != null) {
            for (var loreId : loreSection.getKeys(true)) {
                SlimefunTranslation.debug("Loading lore translation {0}", loreId);
                var lore = loreSection.getString(loreId);
                loreTranslations.put(loreId, lore);
            }
        }

        return Optional.of(new TranslationConfiguration(name, author, lang, itemTranslations, loreTranslations));
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

        this.addon = addon;
        setState(State.REGISTERED);
    }

    public enum State {
        UNREGISTERED,
        REGISTERED
    }
}
