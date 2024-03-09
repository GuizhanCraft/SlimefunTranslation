package net.guizhanss.slimefuntranslation.core.lore;

import net.guizhanss.slimefuntranslation.core.users.User;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface LoreHandler {
    @Nullable
    @ParametersAreNonnullByDefault
    String getLore(User user, String id, String[] args);
}
