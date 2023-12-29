package net.guizhanss.slimefuntranslation.integrations.placeholders;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunLoreExpansion extends AExpansion {
    @Override
    @Nonnull
    public String getName() {
        return "SlimefunLore";
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return "sftrlore";
    }

    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public String onPlaceholderRequest(Player p, String identifier) {
        User user = SlimefunTranslation.getUserService().getUser(p.getUniqueId());
        String[] s = identifier.split("_");

        String id = s[0];
        String[] args = new String[0];
        if (s.length >= 2) {
            args = Arrays.copyOfRange(s, 1, s.length);
        }
        return getResult(user, id, args);
    }
}
