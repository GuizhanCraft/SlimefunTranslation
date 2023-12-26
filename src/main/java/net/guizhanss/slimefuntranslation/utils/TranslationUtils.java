package net.guizhanss.slimefuntranslation.utils;

import java.util.Map;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.slimefuntranslation.core.users.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class TranslationUtils {
    public static final String DEFAULT_LANGUAGE = "en";

    @ParametersAreNonnullByDefault
    public static <V> Optional<V> findTranslation(Map<String, Map<String, V>> map, User user, String key) {
        // find the translations for user's current locale
        var translations = map.get(user.getLocale());
        if (translations != null) {
            var translation = translations.get(key);
            if (translation != null) {
                return Optional.of(translation);
            }
        }

        // try server default locale
        var serverDefault = Slimefun.getLocalization().getDefaultLanguage();
        if (serverDefault != null && !serverDefault.getId().equals(user.getLocale())) {
            translations = map.get(serverDefault.getId());
            if (translations != null) {
                var translation = translations.get(key);
                if (translation != null) {
                    return Optional.of(translation);
                }
            }
        }

        // try default locale
        translations = map.get(DEFAULT_LANGUAGE);
        if (translations != null) {
            return Optional.ofNullable(translations.get(key));
        }

        return Optional.empty();
    }
}
