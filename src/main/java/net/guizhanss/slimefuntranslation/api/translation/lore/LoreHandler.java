package net.guizhanss.slimefuntranslation.api.translation.lore;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.guizhanss.slimefuntranslation.core.users.User;

public interface LoreHandler {
    @Nullable
    @ParametersAreNonnullByDefault
    String getLore(User user, String id, String[] args);
}
