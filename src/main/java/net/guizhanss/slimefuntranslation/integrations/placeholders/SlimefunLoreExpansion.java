package net.guizhanss.slimefuntranslation.integrations.placeholders;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.SlimefunTranslationAPI;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunLoreExpansion extends AExpansion {
    public SlimefunLoreExpansion() {
        super();
        SlimefunTranslationAPI.registerLoreHandler("Machine", (user, id, args) -> {
            if (args.length != 2) return null;
            return getLore(user, "Machine.Format",
                getLore(user, "Machine.TierColor." + args[0]), // color
                getLore(user, "Machine.Tier." + args[0]), // tier
                getLore(user, "Machine.Type." + args[1]) // type
            );
        });
        SlimefunTranslationAPI.registerLoreHandler("Radioactive", (user, id, args) -> {
            if (args.length != 1) return null;
            return getLore(user, "Radioactive", getLore(user, "Radioactivity." + args[0]));
        });
        SlimefunTranslationAPI.registerLoreHandler("Material", (user, id, args) -> {
            if (args.length != 1) return null;
            return getLore(user, "Material", getLore(user, "Materials." + args[0]));
        });
    }

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
        var handler = SlimefunTranslation.getRegistry().getSlimefunLoreHandlers().getOrDefault(id, this::getLore);
        return handler.getLore(user, id, args);
    }
}
