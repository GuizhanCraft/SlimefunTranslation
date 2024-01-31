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
        return switch (id) {
            case "Machine" -> {
                if (args.length != 2) yield null;
                yield getResult(user, "Machine.Format",
                    getResult(user, "Machine.TierColor." + args[0]), // color
                    getResult(user, "Machine.Tier." + args[0]), // tier
                    getResult(user, "Machine.Type." + args[1]) // type
                );
            }
            case "Radioactive" -> {
                if (args.length != 1) yield null;
                yield getResult(user, "Radioactive", getResult(user, "Radioactivity." + args[0]));
            }
            case "Material" -> {
                if (args.length != 1) yield null;
                yield getResult(user, "Material", getResult(user, "Materials." + args[0]));
            }
            default -> getResult(user, id, args);
        };
    }
}
