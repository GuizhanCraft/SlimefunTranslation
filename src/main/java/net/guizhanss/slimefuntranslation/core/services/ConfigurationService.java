package net.guizhanss.slimefuntranslation.core.services;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

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
    private Map<String, String> languageMappings;
    private boolean autoUpdate;
    private boolean debug;

    public ConfigurationService(SlimefunTranslation plugin) {
        config = new AddonConfig(plugin, "config.yml");
        reload();
    }

    public void reload() {
        config.reload();

        autoUpdate = config.getBoolean("auto-update", true);
        debug = config.getBoolean("debug", false);
        languageMappings = ConfigUtils.getMap(config.getConfigurationSection("language-mappings"));

        config.save();
    }

    @Nonnull
    public String getMappedLanguage(@Nonnull String language) {
        Preconditions.checkArgument(language != null, "language cannot be null");
        return SlimefunTranslation.getConfigService().getLanguageMappings().getOrDefault(language, language);
    }
}
