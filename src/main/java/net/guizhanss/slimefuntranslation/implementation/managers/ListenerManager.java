package net.guizhanss.slimefuntranslation.implementation.managers;

import javax.annotation.Nonnull;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerJoinListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerQuitListener;

public final class ListenerManager {
    public ListenerManager(@Nonnull SlimefunTranslation plugin) {
        new PlayerJoinListener(plugin);
        new PlayerQuitListener(plugin);
    }
}
