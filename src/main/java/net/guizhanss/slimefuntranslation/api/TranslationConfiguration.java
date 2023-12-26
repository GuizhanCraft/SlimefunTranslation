package net.guizhanss.slimefuntranslation.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.interfaces.Translation;
import net.guizhanss.slimefuntranslation.implementation.translations.FixedTranslation;
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
    private final List<String> dependencies;
    private final Map<String, Translation> itemTranslations;
    private final Map<String, String> loreTranslations;

    private State state = State.UNREGISTERED;
    private SlimefunAddon addon = null;

    /**
     * Creates a {@link TranslationConfiguration} from a {@link FileConfiguration}.
     *
     * @param config
     *     the {@link FileConfiguration} to create the {@link TranslationConfiguration} from.
     *
     * @return an {@link Optional} of {@link TranslationConfiguration} if the config is valid, otherwise {@code null}.
     */
    @Nonnull
    public static Optional<TranslationConfiguration> fromFileConfiguration(@Nonnull FileConfiguration config) {
        Preconditions.checkArgument(config != null, "config cannot be null");

        String name = config.getString("name", "Unnamed Translation");
        String author = config.getString("author", "SlimefunTranslation");
        String lang = SlimefunTranslation.getConfigService().getMappedLanguage(config.getString("lang", "en"));
        List<String> dependencies = config.getStringList("dependencies");

        for (var dependency : dependencies) {
            if (!SlimefunTranslation.getInstance().getServer().getPluginManager().isPluginEnabled(dependency)) {
                SlimefunTranslation.log(Level.SEVERE, "Translation config \"{0}\" by {1} is missing dependency {2}.", name, author, dependency);
                return Optional.empty();
            }
        }

        var itemsSection = config.getConfigurationSection("translations");
        var loreSection = config.getConfigurationSection("lore");
        if (itemsSection == null && loreSection == null) {
            SlimefunTranslation.log(Level.WARNING, "No translations found in " + name + " by " + author);
            return Optional.empty();
        }
        Map<String, Translation> itemTranslations = new HashMap<>();
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

                // lore replacements
                Map<Integer, String> replacementMap = new HashMap<>();
                if (itemSection.contains("lore-replacements")) {
                    try {
                        Map<String, String> replacements = ConfigUtils.getMap(itemSection.getConfigurationSection("lore-replacements"));
                        for (var entry : replacements.entrySet()) {
                            replacementMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
                        }
                    } catch (NumberFormatException | NullPointerException ex) {
                        SlimefunTranslation.log(Level.SEVERE, "Invalid lore replacements of item {0} in translation {1} by {2}", itemId, name, author);
                        return Optional.empty();
                    }
                }

                // check name
                boolean checkName = itemSection.getBoolean("check-name", false);

                var translation = new FixedTranslation(displayName, lore, replacementMap, checkName);
                itemTranslations.put(itemId, translation);
            }
        }

        if (loreSection != null) {
            for (var loreId : loreSection.getKeys(false)) {
                SlimefunTranslation.debug("Loading lore translation {0}", loreId);
                var lore = loreSection.getString(loreId);
                loreTranslations.put(loreId, lore);
            }
        }

        return Optional.of(new TranslationConfiguration(name, author, lang, dependencies, itemTranslations, loreTranslations));
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
