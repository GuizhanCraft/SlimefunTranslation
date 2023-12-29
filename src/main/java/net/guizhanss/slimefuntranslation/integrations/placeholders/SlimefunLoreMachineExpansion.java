package net.guizhanss.slimefuntranslation.integrations.placeholders;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

public class SlimefunLoreMachineExpansion extends AExpansion {
    @Override
    @Nonnull
    public String getName() {
        return "SlimefunLore-Machine";
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return "sftrloremachine";
    }

    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public String onPlaceholderRequest(Player p, String identifier) {
        User user = SlimefunTranslation.getUserService().getUser(p.getUniqueId());
        String[] args = identifier.split("_");

        if (args.length < 2) return null;
        return getResult(user, "Machine.Tier." + args[0]) + " " + getResult(user, "Machine.Type." + args[1]);
    }
}
