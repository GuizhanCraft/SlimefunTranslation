package net.guizhanss.slimefuntranslation.integrations.placeholders;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.guizhanss.slimefuntranslation.utils.TranslationUtils;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunLoreExpansion extends PlaceholderExpansion {
    @Override
    @Nonnull
    public String getName() {
        return "SlimefunLore";
    }

    @Override
    @Nonnull
    public String getAuthor() {
        return "SlimefunTranslation";
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return "sftrlore";
    }

    @Override
    @Nonnull
    public String getVersion() {
        return SlimefunTranslation.getInstance().getPluginVersion();
    }

    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public String onPlaceholderRequest(Player p, String identifier) {
        User user = SlimefunTranslation.getUserService().getUser(p.getUniqueId());
        String[] s = identifier.split("_");

        String id = s[0];
        String[] args;
        if (s.length < 2) {
            args = new String[0];
        } else {
            args = Arrays.copyOfRange(s, 1, s.length);
        }
        var transl = TranslationUtils.findTranslation(
            SlimefunTranslation.getRegistry().getLoreTranslations(), user, id);
        return transl.map(translation -> MessageFormat.format(translation, (Object[]) args)).orElse(null);
    }
}
