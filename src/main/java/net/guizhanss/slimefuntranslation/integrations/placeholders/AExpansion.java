package net.guizhanss.slimefuntranslation.integrations.placeholders;

import java.text.MessageFormat;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.ColorUtils;

public abstract class AExpansion extends PlaceholderExpansion {
    @Override
    @Nonnull
    public String getAuthor() {
        return "SlimefunTranslation";
    }

    @Override
    @Nonnull
    public String getVersion() {
        return SlimefunTranslation.getInstance().getPluginVersion();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    protected String getResult(User user, String id, String... args) {
        var translation = SlimefunTranslation.getTranslationService().getLore(user, id, true);
        return ColorUtils.color(
            SlimefunTranslation.getIntegrationService().applyPlaceholders(
                user, MessageFormat.format(translation, (Object[]) args)
            )
        );
    }
}
