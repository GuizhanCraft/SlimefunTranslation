package net.guizhanss.slimefuntranslation.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.guizhanss.slimefuntranslation.implementation.translations.FixedTranslation;
import net.guizhanss.slimefuntranslation.utils.ConfigUtils;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.interfaces.Translation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class TranslationConfiguration {
    private final String name;
    private final String author;
    private final String lang;
    private final List<String> dependencies;
    private final Map<String, Translation> translations;

    @Setter(AccessLevel.PRIVATE)
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

        var section = config.getConfigurationSection("translations");
        if (section == null) {
            SlimefunTranslation.log(Level.WARNING, "No translations found in " + name + " by " + author);
            return Optional.empty();
        }
        SlimefunTranslation.log(Level.INFO, "Loading translation configuration \"{0}\" by {1}, language: {2}", name, author, lang);
        Map<String, Translation> translations = new HashMap<>();
        for (var itemId : section.getKeys(false)) {
            SlimefunTranslation.debug("Loading translation {0}", itemId);
            var itemSection = section.getConfigurationSection(itemId);
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
            translations.put(itemId, translation);
        }
        return Optional.of(new TranslationConfiguration(name, author, lang, dependencies, translations));
    }

    public void register(@Nonnull SlimefunAddon addon) {
        if (state != State.UNREGISTERED) {
            throw new IllegalStateException("TranslationConfiguration is already registered");
        }

        var allTranslations = SlimefunTranslation.getRegistry().getTranslations();
        allTranslations.putIfAbsent(lang, new HashMap<>());
        var currentTranslations = allTranslations.get(lang);
        currentTranslations.putAll(translations);

        this.addon = addon;
        setState(State.REGISTERED);
    }

    public enum State {
        UNREGISTERED,
        REGISTERED
    }
}
