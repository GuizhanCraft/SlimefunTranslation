package net.guizhanss.slimefuntranslation.core.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.Material;

import net.guizhanss.guizhanlib.slimefun.addon.AddonConfig;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.utils.ConfigUtils;

import lombok.AccessLevel;
import lombok.Getter;

@SuppressWarnings("ConstantConditions")
@Getter
public final class ConfigurationService {
    @Getter(AccessLevel.NONE)
    private final AddonConfig config;
    private boolean autoUpdate;
    private boolean debug;
    private boolean interceptSearch;
    private Map<String, String> languageMappings;
    private Set<String> disabledLanguages;
    private Set<Material> partialOverrideMaterials;
    private Set<String> disabledItems;

    public ConfigurationService(SlimefunTranslation plugin) {
        config = new AddonConfig(plugin, "config.yml");
        reload();
    }

    public void reload() {
        config.reload();
        config.addMissingKeys();

        autoUpdate = config.getBoolean("auto-update", true);
        debug = config.getBoolean("debug", false);
        interceptSearch = config.getBoolean("intercept-search", true);
        languageMappings = ConfigUtils.getMap(config.getConfigurationSection("language-mappings"));
        disabledLanguages = new HashSet<>(config.getStringList("disabled-languages"));
        partialOverrideMaterials = ConfigUtils.parseMaterials(config.getStringList("partial-override-materials"));
        disabledItems = new HashSet<>(config.getStringList("disabled-items"));

        config.save();
    }

    @Nonnull
    public String getMappedLanguage(@Nonnull String language) {
        Preconditions.checkArgument(language != null, "language cannot be null");
        return SlimefunTranslation.getConfigService().getLanguageMappings().getOrDefault(language, language);
    }
}
