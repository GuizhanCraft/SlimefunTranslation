package net.guizhanss.slimefuntranslation.utils;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.slimefuntranslation.core.users.User;

import lombok.experimental.UtilityClass;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public final class TranslationUtils {
    public static final String DEFAULT_LANGUAGE = "en";

    /**
     * Find the translation from Registry, using the given key for the give {@link User}.
     * If the {@link User} is not provided, will try the server default locale, then the default locale.
     * @param map The {@link Map} from Registry.
     * @param user The {@link User} to find the translation for.
     * @param key The key to find the translation for.
     * @return An {@link Optional} of the translation.
     * @param <V> The key type of translation.
     */
    @Nonnull
    public static <V> Optional<V> findTranslation(@Nonnull Map<String, Map<String, V>> map, @Nullable User user, @Nonnull String key) {
        Preconditions.checkArgument(map != null, "map cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");

        if (user == null) {
            return findDefaultTranslation(map, key);
        }

        // find the translations for user's current locale
        var translations = map.get(user.getLocale());
        if (translations != null) {
            var translation = translations.get(key);
            if (translation != null) {
                return Optional.of(translation);
            }
        }

        return findDefaultTranslation(map, key);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static <V> Optional<V> findDefaultTranslation(Map<String, Map<String, V>> map, String key) {
        Preconditions.checkArgument(map != null, "map cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");

        Map<String, V> translations;
        // try server default locale
        var serverDefault = Slimefun.getLocalization().getDefaultLanguage();
        if (serverDefault != null) {
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
